package com.giulia.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.giulia.model.entity.Lancamento;
import com.giulia.model.enums.StatusLancamento;
import com.giulia.model.enums.TipoLancamento;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();

		lancamento = repository.save(lancamento);

		Assertions.assertThat(lancamento.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = entityManager.persist(lancamento); // persistir na base de dados

		lancamento = entityManager.find(Lancamento.class, lancamento.getId()); // buscar na base de dados

		repository.delete(lancamento);

		Lancamento encontrarDeletado = entityManager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(encontrarDeletado).isNull();

	}

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = entityManager.persist(lancamento); // retorna um já com id
		
		//acao
		lancamento.setAno(2018);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		repository.save(lancamento); //atualizando
		
		Lancamento atualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		Assertions.assertThat(atualizado.getAno()).isEqualTo(2018);
		Assertions.assertThat(atualizado.getDescricao()).isEqualTo("Teste Atualizar");
		Assertions.assertThat(atualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

	}

	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarLancamento();
		lancamento = entityManager.persist(lancamento); // retorna um já com id
		
		Optional<Lancamento> encontrado = repository.findById(lancamento.getId());
		
		Assertions.assertThat(encontrado.isPresent()).isTrue();
		
	}

	public Lancamento criarLancamento() {
		return Lancamento.builder().ano(2019).mes(1).descricao("qualquer").valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now()).build();
	}

}
