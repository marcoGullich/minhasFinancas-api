package br.com.gullich.minhasFinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.gullich.minhasFinancas.model.entity.Usuario;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Test
	public void deveVerificarAExistenciaDoEmail(){
		//cenario
		Usuario usuario = Usuario.builder().nome("Usuario").email("email@email.com").build();
		repository.save(usuario);
		
		//ação/execução
		boolean resultado = repository.existsByEmail("email@email.com");
		
		//verificação
		Assertions.assertThat(resultado).isTrue();
		repository.deleteAll();
	}
	
	
	@Test
	public void devePersistirUsuarioBaseDados(){
		Usuario usuario = this.criarUsuario();
		
		Usuario usuarioBanco = repository.save(usuario);
		
		Assertions.assertThat(usuarioBanco.getId()).isNotNull();
		repository.deleteAll();
	}
	
	@Test
	public void deveBuscarUsuarioPorEmail(){
		Usuario usuario = this.criarUsuario();
		this.repository.save(usuario);
		
		Optional<Usuario> usuarioSalvo = repository.findByEmail("email@email.com");
		
		Assertions.assertThat(usuarioSalvo.isPresent()).isTrue();
		repository.deleteAll();
		
	}
	
	@Test
	public void deveRetornarVazioUsuarioPorEmailNaoExisteBase(){
		this.repository.deleteAll();
		
		Optional<Usuario> usuarioSalvo = repository.findByEmail("email@email.com");
		
		Assertions.assertThat(usuarioSalvo.isPresent()).isFalse();
		
	}
	
	@Test
	public void deveRetornarFalseQuandoNaoHouverEmail(){
		repository.deleteAll();
		
		boolean resultado = repository.existsByEmail("email@email.com");
		
		Assertions.assertThat(resultado).isFalse();
	}
	
	public Usuario criarUsuario(){
		return Usuario.builder().nome("usuario").email("email@email.com").
				senha("senha").build();
	}
}
