package br.com.gullich.minhasFinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.gullich.minhasFinancas.exception.ErroAutenticacaoException;
import br.com.gullich.minhasFinancas.exception.RegraNegocioException;
import br.com.gullich.minhasFinancas.model.entity.Lancamento;
import br.com.gullich.minhasFinancas.model.entity.Usuario;
import br.com.gullich.minhasFinancas.model.enums.StatusLancamento;
import br.com.gullich.minhasFinancas.model.repository.LancamentoRepository;
import br.com.gullich.minhasFinancas.model.repository.LancamentoRepositoryTest;
import br.com.gullich.minhasFinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceImplTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	
	@Test
	public void deveSalvarUmLancamento(){
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarMassa();
		Mockito.doNothing().when(service).validar(lancamentoASalvar); //Não faça nada quando o service validar o lancamento para salvar. Não vai lançar erro qdo o service chamar o método de validar.
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarMassa();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo); //Fazendo o mock do save, e manipulando o retorno do método com o objeto lancamentoSalvo.
		
		Lancamento lancamento = service.salva(lancamentoASalvar);
		
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao(){
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarMassa();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar); //Lançar uma Exception qdo o service chamar o validar o lancamento para salvar.
		
		Assertions.catchThrowableOfType(() -> this.service.salva(lancamentoASalvar), RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento(){
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarMassa();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamentoSalvo); //Não faça nada quando o service validar o lancamento para salvar. Não vai lançar erro qdo o service chamar o método de validar.
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo); //Fazendo o mock do save, e manipulando o retorno do método com o objeto lancamentoSalvo.
		
		service.atualizar(lancamentoSalvo);
		
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void naoDeveAtualizarUmLancamentoQuandoHouverErroDeValidacao(){
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		
		Assertions.catchThrowableOfType(() -> this.service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento(){
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		lancamento.setId(1l);
		
		this.service.deletar(lancamento);
		
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void naoDeveDeletarUmLancamentoQuandoNaoHouverId(){
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos(){
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> buscar = service.buscar(lancamento);
		
		Assertions.assertThat(buscar).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDoLancamento(){
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId(){
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoUmLancamentoNaoExistir(){
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveValidarUmLancamento(){
		Lancamento lancamento = LancamentoRepositoryTest.criarMassa();
		lancamento.setUsuario(new Usuario());
		
		lancamento.setDescricao("");
		Throwable exception = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

		lancamento.setDescricao(null);
		Throwable exception1 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception1).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

		lancamento.setDescricao("Teste");
		lancamento.setMes(null);
		Throwable exception2 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception2).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setDescricao("Teste");
		lancamento.setMes(0);
		Throwable exception3 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception3).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setDescricao("Teste");
		lancamento.setMes(13);
		Throwable exception4 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception4).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

		lancamento.setDescricao("Teste");
		lancamento.setMes(12);
		lancamento.setAno(123);
		Throwable exception5 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception5).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

		lancamento.setDescricao("Teste");
		lancamento.setMes(12);
		lancamento.setAno(1234);
		lancamento.setValor(BigDecimal.valueOf(0));
		lancamento.getUsuario().setId(1l);
		Throwable exception6 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception6).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

		lancamento.setDescricao("Teste");
		lancamento.setMes(12);
		lancamento.setAno(1234);
		lancamento.setValor(BigDecimal.valueOf(10l));
		lancamento.getUsuario().setId(null);
		Throwable exception7 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception7).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
		
		lancamento.setDescricao("Teste");
		lancamento.setMes(12);
		lancamento.setAno(1234);
		lancamento.setValor(BigDecimal.valueOf(10l));
		lancamento.getUsuario().setId(1l);
		lancamento.setTipo(null);
		Throwable exception8 = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(exception8).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
	}
}
