package com.giulia.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.giulia.model.entity.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test") //pega o application.properties com test
@DataJpaTest //cria isntancia do banco na memoria e depois deleta.
@AutoConfigureTestDatabase(replace = Replace.NONE) //anotação que NÃO desconfigura a base na memória, por conta da anotação DATAJPA	
public class UsuarioRepositoryTeste {

	@Autowired
	UsuarioRepository repository;
	
	@Autowired //classe que opera na base, para eliminar o antigo repository.
	TestEntityManager entityManager;

	@Test
	public void deveVerificarExistenciaEmail() {
		// cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		// ação/execução
		boolean result = repository.existsByEmail("usuario@email.com");

		// verificação
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoSeEmailNaoCadastrado() {
		//cenário
		
		//ação
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//verificação
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUsuarioNaBaseDeDados() {
		//por "persistir" em uma base, devemos gravar na base de verdade.	
		//cenario
		Usuario usuario = criarUsuario();
		
		//ação
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUsuarioPorEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//verificação
		Optional<Usuario> result = repository.findByEmail("email@email.com");
		
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarUsuarioInexistenteVazio() {
		//verificação
		Optional<Usuario> result = repository.findByEmail("email@email.com");
		
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	
	public static Usuario criarUsuario() {
		return Usuario.builder().email("email@email.com").nome("usuario").senha("123").build();
	}
}
