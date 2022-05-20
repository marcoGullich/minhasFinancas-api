package br.com.gullich.minhasFinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.gullich.minhasFinancas.exception.ErroAutenticacaoException;
import br.com.gullich.minhasFinancas.exception.RegraNegocioException;
import br.com.gullich.minhasFinancas.model.entity.Usuario;
import br.com.gullich.minhasFinancas.model.repository.UsuarioRepository;
import br.com.gullich.minhasFinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	private UsuarioServiceImpl service;
	
	@MockBean
	private UsuarioRepository repository;
	
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail(){
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		service.validarEmail("emial@email.com");
	}
	
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroValidarEmailCadastrado(){
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		service.validarEmail("email@email.com");
	}
	
	@Test(expected = Test.None.class)
	public void deveAltenticarUsuarioComSucesso(){
		Mockito.when(repository.findByEmail(this.criarMassaTeste().getEmail())).
			thenReturn(Optional.of(this.criarMassaTeste()));
		
		Usuario resultado = service.autenticar(this.criarMassaTeste().getEmail(), this.criarMassaTeste().getSenha());
		
		Assertions.assertThat(resultado).isNotNull();
	}
	
	@Test(expected = ErroAutenticacaoException.class)
	public void deveLancarErroQuandoNaoEncontrarUsuario(){
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		service.autenticar("email@email.com", "senha");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaInvalida() {
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(this.criarMassaTeste()));
		
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar(this.criarMassaTeste().getEmail(), "123"));
		
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Senha invalida.");
		
	}
	
	@Test(expected = Test.None.class)
	public void deveSalvarUsuario(){
		
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(this.criarMassaTeste());
		
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo(this.criarMassaTeste().getEmail());
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo(this.criarMassaTeste().getSenha());
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUsuarioEmailCadastrado(){
		Usuario usuario = this.criarMassaTeste();
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(this.criarMassaTeste().getEmail());
		
		service.salvarUsuario(usuario);
		
		Mockito.verify( repository, Mockito.never()).save(usuario);
	}
	
	
	public Usuario criarMassaTeste(){
		String email = "email@email.com";
		String senha = "senha";
	
		return Usuario.builder()
				.email(email)
				.senha(senha)
				.build();
	}
}
