package com.giulia.service;

import java.util.Optional;

import com.giulia.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	//recebe como parâmetro um usuário não salvo
	Usuario salvarUsuario(Usuario usuario);
	
	//Se o email já ta cadastrado, não pode cadastrar de novo.
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);
	
}
