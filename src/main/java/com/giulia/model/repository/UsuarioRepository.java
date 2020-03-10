package com.giulia.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giulia.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	// possui os métodos com os bancos de dados, mas não a regra de negócios.

	// optional é porque retorna alguma coisa, ou não
	Optional<Usuario> findByEmail(String email); // query methods(feitos pelo spring)

	boolean existsByEmail(String email);
}
