package br.com.gullich.minhasFinancas.service;

import java.util.List;

import br.com.gullich.minhasFinancas.model.entity.Lancamento;
import br.com.gullich.minhasFinancas.model.enums.StatusLancamento;

public interface LancamentoSerive {

	Lancamento salva(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
}
