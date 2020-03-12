package com.giulia.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.giulia.exception.RegraNegocioException;
import com.giulia.model.entity.Lancamento;
import com.giulia.model.entity.Usuario;
import com.giulia.model.enums.StatusLancamento;
import com.giulia.model.enums.TipoLancamento;
import com.giulia.model.repository.LancamentoRepository;
import com.giulia.service.impl.LancamentoServiceImp;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImp service;

	@MockBean // cria instância mockada
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		// cenario
		Lancamento lancamentoASalvar = criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = criarLancamento();
		lancamentoSalvo.setId(10l);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// execução
		Lancamento salvo = service.salvar(lancamentoASalvar);

		Assertions.assertThat(salvo.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(salvo.getStatus()).isEqualTo(StatusLancamento.PENDENTE);

	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		Lancamento lancamento = criarLancamento();
		lancamento.setDescricao(null);
		Assertions.catchThrowableOfType(() -> service.salvar(lancamento), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		Mockito.doNothing().when(service).validar(lancamento);

		Mockito.when(repository.save(lancamento)).thenReturn(lancamento);

		// execução
		Lancamento salvo = service.atualizar(lancamento);

		Mockito.verify(repository, Mockito.times(1)).save(salvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarSemId() {
		Lancamento lancamento = criarLancamento();

		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);

	}

	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);

		service.deletar(lancamento);

		Mockito.verify(repository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoDeletarUmLancamento() {
		Lancamento lancamento = criarLancamento();

		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = new ArrayList<Lancamento>();
		lista.add(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		List<Lancamento> resultado = service.buscar(lancamento);

		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);

	}

	@Test
	public void deveAtualizarStatus() {
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento status = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		service.atualizarStatus(lancamento, status);

		Assertions.assertThat(lancamento.getStatus()).isEqualTo(status);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterLancamentoPorId() {
		Long id = 1l;
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		Optional<Lancamento> resultado = service.obterPorId(id);

		Assertions.assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioAoBuscarLancamentoPorIdInexistente() {
		Long id = 2l;
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		Optional<Lancamento> resultado = service.obterPorId(id);

		Assertions.assertThat(resultado.isPresent()).isFalse();
	}

	@Test
	public void deveLancarErrosAoValidarOLancamento() {
		Lancamento lancamento = new Lancamento();
		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe uma Descrição válida");

		lancamento.setDescricao("não nula");
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido");

		lancamento.setMes(6);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido");

		lancamento.setAno(2019);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário");

		lancamento.setUsuario(Usuario.builder().id(1l).build());
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido");

		lancamento.setValor(BigDecimal.TEN);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo válido");
	}

	public Lancamento criarLancamento() {
		return Lancamento.builder().ano(2019).mes(1).descricao("qualquer").valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now()).build();
	}

}
