package com.giulia.service;

import org.junit.Test;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.giulia.exception.ErroAutenticacao;
import com.giulia.exception.RegraNegocioException;
import com.giulia.model.entity.Usuario;
import com.giulia.model.repository.UsuarioRepository;
import com.giulia.service.impl.UsuarioServiceImp;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

//	Utilização de mocks pois esses testes são de UNIDADE, devem validar apenas comportamento de métodos,
//	a validação de intergação com o banco é feito no outro teste!

	@SpyBean
	UsuarioServiceImp service;

	@MockBean // cria instância mockada
	UsuarioRepository repository;

	// espera que nenhuma excessão seja lançada
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		service.validarEmail("email@email.com");
	}

	// espera uma excessão
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmail() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		service.validarEmail("usuario@email.com");
	}

	@Test
	public void deveAutenticarUsuarioComSucesso() {
		// cenário
		String email = "email@email.com";
		String senha = "123";

		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

		// acao
		Usuario result = service.autenticar(email, senha);
		Assertions.assertThat(result).isNotNull();
	}

	@Test
	public void deveLancarErroAoNaoEncontrarUsuarioComEmail() {
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		Throwable ex = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
		Assertions.assertThat(ex).isInstanceOf(ErroAutenticacao.class).hasMessage("Email Inválido");
	}

	@Test
	public void deveLancarErroAoNaoEncontrarUsuarioComSenha() {
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// acao
		Throwable ex = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
		Assertions.assertThat(ex).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha Inválida");
	}

	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuario() {
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString()); // não lança erros
		Usuario usuario = Usuario.builder().email("email.com").senha("123").nome("nome").id(1l).build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		// acao
		Usuario result = service.salvarUsuario(new Usuario());
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getId()).isEqualTo(1l);
		Assertions.assertThat(result.getEmail()).isEqualTo("email.com");
		Assertions.assertThat(result.getSenha()).isEqualTo("123");
		Assertions.assertThat(result.getNome()).isEqualTo("nome");
	}

	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		// cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		// acao
		service.salvarUsuario(usuario);
		// verificação(que nunca salvou o usuario)
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
}
