package br.com.gullich.minhasFinancas.api.resource;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gullich.minhasFinancas.api.dto.UsuarioDTO;
import br.com.gullich.minhasFinancas.exception.ErroAutenticacaoException;
import br.com.gullich.minhasFinancas.exception.RegraNegocioException;
import br.com.gullich.minhasFinancas.model.entity.Usuario;
import br.com.gullich.minhasFinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {
	
	private UsuarioService service;
	
	public UsuarioResource(UsuarioService service){
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto){
		Usuario usuario = preparaEntidadeParaDTO(dto);
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar( @RequestBody UsuarioDTO dto ){
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacaoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	private Usuario preparaEntidadeParaDTO(UsuarioDTO dto){
		return Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();
	}
	
}
