package br.com.gullich.minhasFinancas.api.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gullich.minhasFinancas.api.dto.UsuarioDTO;
import br.com.gullich.minhasFinancas.exception.ErroAutenticacaoException;
import br.com.gullich.minhasFinancas.exception.RegraNegocioException;
import br.com.gullich.minhasFinancas.model.entity.Usuario;
import br.com.gullich.minhasFinancas.service.LancamentoSerive;
import br.com.gullich.minhasFinancas.service.UsuarioService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {

	static final String API = "/api/usuarios";
	
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoSerive lancamentoService; 
	
	@Test
	public void deveAutenticarUmUsuario()throws Exception {
		
		Usuario usuario = Usuario.builder()
				.id(1l)
				.email(this.massaTeste().getEmail())
				.senha(this.massaTeste().getSenha())
				.build();
		
		Mockito.when(service.autenticar(this.massaTeste().getEmail(), this.massaTeste().getSenha())).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(this.massaTeste());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.post(API.concat("/autenticar"))
		.accept(JSON)
		.contentType(JSON)
		.content(json);
	
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao()throws Exception {
		
		Mockito.when(service.autenticar(this.massaTeste().getEmail(), this.massaTeste().getSenha())).thenThrow(ErroAutenticacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(this.massaTeste());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API.concat("/autenticar"))
				.accept(JSON)
				.contentType(JSON)
				.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveCriarUmNovoUsuario()throws Exception {
		
		Usuario usuario = Usuario.builder()
				.id(1l)
				.email(this.massaTeste().getEmail())
				.senha(this.massaTeste().getSenha())
				.build();
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(this.massaTeste());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.post(API)
		.accept(JSON)
		.contentType(JSON)
		.content(json);
	
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	public void deveRetornarBadRequestAoTestarCriarNovoUsuarioInvalido()throws Exception {
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(this.massaTeste());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.post(API)
		.accept(JSON)
		.contentType(JSON)
		.content(json);
	
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	
	private UsuarioDTO massaTeste(){
		return UsuarioDTO.builder()
				.nome("Marco")
				.email("teste@teste.com")
				.senha("12345")
				.build();
	}
}
