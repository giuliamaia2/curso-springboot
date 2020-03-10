package com.giulia.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.giulia.exception.RegraNegocioException;
import com.giulia.model.entity.Lancamento;
import com.giulia.model.enums.StatusLancamento;
import com.giulia.model.repository.LancamentoRepository;
import com.giulia.service.LancamentoService;

@Service
public class LancamentoServiceImp implements LancamentoService {

	private LancamentoRepository repository;

	public LancamentoServiceImp(LancamentoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional // importante para fazer operações na base, ele commita se ouver sucesso, da
					// rollback se não.
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId()); // garante que o lancamento tem id, ou lanca um nullpointer
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		Example<Lancamento> example = Example.of(lancamentoFiltro, // pega as propriedades populadas
				ExampleMatcher.matching() // moço que ajuda na construção dessa query.
						.withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING)); // busca descrição
																						// seletivamente.

		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {

		// validando os campos obrigatórios da nossa tabela...
		if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida");
		}

		if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um Mês válido");
		}

		if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido");
		}

		if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informa um Usuário");
		}

		if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um Valor válido");
		}

		if (lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um Tipo válido");
		}

	}

}
