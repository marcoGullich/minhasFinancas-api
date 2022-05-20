package br.com.gullich.minhasFinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gullich.minhasFinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	Optional<Usuario> findByEmail(String email);
	
	Optional<Usuario> findByEmailAndNome(String email, String nome);
	
	boolean existsByEmail(String email);
	
}
