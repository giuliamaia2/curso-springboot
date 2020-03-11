package com.giulia.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.giulia.dto.AtualizaStatusDto;
import com.giulia.dto.LancamentoDto;
import com.giulia.exception.RegraNegocioException;
import com.giulia.model.entity.Lancamento;
import com.giulia.model.entity.Usuario;
import com.giulia.model.enums.StatusLancamento;
import com.giulia.model.enums.TipoLancamento;
import com.giulia.service.LancamentoService;
import com.giulia.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamento")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService service;
	private final UsuarioService usuarioService;

	@PutMapping("/atualizar-status/{id}")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDto dto) {
		return service.obterPorId(id).map( entity -> {
			
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			
			if (statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Status inválido!");
			}
			
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);

			} catch (Exception e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(
				() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));

	}

	@GetMapping("/buscar")
	public ResponseEntity<Object> buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "usuario") Long idUsuario) {
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);

		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar consulta.");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}

	@PostMapping("/salvar")
	public ResponseEntity<Object> salvar(@RequestBody LancamentoDto dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity<Object>(entidade, HttpStatus.CREATED);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/atualizar/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDto dto) {
		service.obterPorId(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return new ResponseEntity<Object>(lancamento, HttpStatus.OK);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(
				() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
		return null;
	}

	@DeleteMapping("/deletar/{id}")
	public ResponseEntity<Object> deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entidade -> {
			service.deletar(entidade);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseGet(
				() -> new ResponseEntity<Object>("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}

	public Lancamento converter(LancamentoDto dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não existe para id informado"));

		lancamento.setUsuario(usuario);
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		return lancamento;
	}

}
