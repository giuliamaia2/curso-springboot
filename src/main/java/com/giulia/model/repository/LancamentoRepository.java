package com.giulia.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giulia.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
