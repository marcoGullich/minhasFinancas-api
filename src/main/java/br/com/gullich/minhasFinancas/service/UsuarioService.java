package br.com.gullich.minhasFinancas.service;

import br.com.gullich.minhasFinancas.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(final String email, final String senha);
	
	Usuario salvarUsuario(final Usuario usuario);
	
	void validarEmail(String email);
	
	Usuario obterPorId(Long idUsuario);
	
}
