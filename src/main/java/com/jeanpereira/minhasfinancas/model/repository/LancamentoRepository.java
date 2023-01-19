package com.jeanpereira.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanpereira.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
