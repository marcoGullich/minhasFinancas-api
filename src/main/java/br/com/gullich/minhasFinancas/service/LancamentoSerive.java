package br.com.gullich.minhasFinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.gullich.minhasFinancas.model.entity.Lancamento;
import br.com.gullich.minhasFinancas.model.enums.StatusLancamento;

public interface LancamentoSerive {

	Lancamento salva(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Optional<Lancamento> obterPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
