package br.com.gullich.minhasFinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gullich.minhasFinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

	
}
