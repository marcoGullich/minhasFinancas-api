package br.com.gullich.minhasFinancas.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.gullich.minhasFinancas.exception.ErroAutenticacaoException;
import br.com.gullich.minhasFinancas.exception.RegraNegocioException;
import br.com.gullich.minhasFinancas.model.entity.Usuario;
import br.com.gullich.minhasFinancas.model.repository.UsuarioRepository;
import br.com.gullich.minhasFinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private UsuarioRepository repository;
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()){
			throw new ErroAutenticacaoException("Usuário não encontrado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)){
			throw new ErroAutenticacaoException("Senha invalida.");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		this.validarEmail(usuario.getEmail());
		return this.repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe){
			throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
		}
		
	}
}
