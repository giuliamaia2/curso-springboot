package com.giulia.controllers;

import com.giulia.exception.ErroAutenticacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.giulia.dto.UsuarioDto;
import com.giulia.exception.RegraNegocioException;
import com.giulia.model.entity.Usuario;
import com.giulia.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private UsuarioService service;

	private UsuarioController(UsuarioService service) {
		this.service = service;
	}

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

}
