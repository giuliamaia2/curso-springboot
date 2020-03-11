package com.giulia.controllers;

import com.giulia.exception.ErroAutenticacao;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.giulia.dto.UsuarioDto;
import com.giulia.exception.RegraNegocioException;
import com.giulia.model.entity.Usuario;
import com.giulia.service.LancamentoService;
import com.giulia.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService service;
	private final LancamentoService lancamentoService;

	@PostMapping("/autenticar")
	public ResponseEntity<Object> autenticar(@RequestBody UsuarioDto dto) {
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Object> salvar(@RequestBody UsuarioDto dto) {
		Usuario usuario = Usuario.builder().nome(dto.getNome()).senha(dto.getSenha()).email(dto.getEmail()).build();
		try {
			Usuario salvo = service.salvarUsuario(usuario);
			return new ResponseEntity<Object>(salvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/saldo/{id}")
	public ResponseEntity<Object> obterSaldo(@PathVariable("id") Long id){
		Optional<Usuario> usuario = service.obterPorId(id);
		
		if(!usuario.isPresent()) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}

}
