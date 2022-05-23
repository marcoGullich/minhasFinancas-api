package br.com.gullich.minhasFinancas.api.resource;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gullich.minhasFinancas.api.dto.AtualizaStatusDTO;
import br.com.gullich.minhasFinancas.api.dto.LancamentoDTO;
import br.com.gullich.minhasFinancas.exception.RegraNegocioException;
import br.com.gullich.minhasFinancas.model.entity.Lancamento;
import br.com.gullich.minhasFinancas.model.entity.Usuario;
import br.com.gullich.minhasFinancas.model.enums.StatusLancamento;
import br.com.gullich.minhasFinancas.model.enums.TipoLancamento;
import br.com.gullich.minhasFinancas.service.LancamentoSerive;
import br.com.gullich.minhasFinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoSerive service;
	private final UsuarioService usuarioService;


	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto){
		try {
			Lancamento lancamento = this.converter(dto);
			lancamento = service.salva(lancamento);
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto){
		return service.obterPorId(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> 
			new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar( @PathVariable("id") Long id){
		return service.obterPorId(id).map(entity -> {
			service.deletar(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> 
		new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Usuario usuario = this.usuarioService.obterPorId(idUsuario);
		if(Objects.isNull(usuario)){
			return ResponseEntity.badRequest().body("Usuário não encontrado na base.");
		} else {
			lancamentoFiltro.setUsuario(usuario);
		}
		List<Lancamento> lancamento = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamento);
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto){
		
		return service.obterPorId(id).map(entity ->{
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			if(Objects.isNull(statusSelecionado)){
				return ResponseEntity.badRequest().body("Não foi possivel atualizar o status do lancamento.");
			}
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
				
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() ->
		new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
	}
	
	private Lancamento converter(LancamentoDTO dto){
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setId(dto.getId());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = this.usuarioService.obterPorId(dto.getUsuario());
		if(Objects.nonNull(usuario)){
			lancamento.setUsuario(usuario);
		} else {
			throw new RegraNegocioException("Usuário não cadastrado.");
		}
		
		if(Objects.nonNull(dto.getTipo())){
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if(Objects.nonNull(dto.getStatus())){
		lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		return lancamento;
	}
}
