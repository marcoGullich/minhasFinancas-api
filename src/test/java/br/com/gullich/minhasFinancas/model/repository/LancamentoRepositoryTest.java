package br.com.gullich.minhasFinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.gullich.minhasFinancas.model.entity.Lancamento;
import br.com.gullich.minhasFinancas.model.enums.StatusLancamento;
import br.com.gullich.minhasFinancas.model.enums.TipoLancamento;

//@RunWith(SpringRunner.class)
//@DataJdbcTest //Anotação para testes de integração
//@SpringBootTest
//@AutoConfigureTestDatabase(replace = Replace.NONE) //Anotação para não sobreEscrever as configurações do ambiente de teste
//@ActiveProfiles("test") //Anotação para ativar o profile de teste(application-test.properties)
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	private LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento(){
		Lancamento lancamento = criarMassa();
		
		lancamento = this.repository.save(lancamento);
		
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}
	
	
	@Test
	public void deveDeletarUmLancamento(){
		this.repository.deleteAll();
		
		Lancamento lancamento = Lancamento.builder()
								.ano(2022)
								.mes(05)
								.descricao("Teste")
								.tipo(TipoLancamento.RECEITA)
								.valor(BigDecimal.valueOf(100l))
								.build();
		repository.save(lancamento);
		
		Assertions.assertThat(lancamento.getId()).isNotNull();
		Assertions.assertThat(repository.count()).isEqualTo(1l);
		
		this.repository.deleteById(lancamento.getId());
		Assertions.assertThat(repository.count()).isEqualTo(0);
	}
	
	@Test
	public void deveAtualizarUmLancamente(){
		
		Lancamento lancamento = this.criarMassa();
		this.repository.save(lancamento);
		
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setValor(BigDecimal.valueOf(200));
		lancamento.setStatus(StatusLancamento.EFETIVADO);
		
		this.repository.save(lancamento);
		Optional<Lancamento> lancamentoAtualizado = this.repository.findById(lancamento.getId());
		
		Assertions.assertThat(lancamentoAtualizado.get().getValor()).isEqualTo("200.00");
		Assertions.assertThat(lancamentoAtualizado.get().getDescricao()).isEqualTo("Teste Atualizar");
		Assertions.assertThat(lancamentoAtualizado.get().getStatus()).isEqualTo(StatusLancamento.EFETIVADO);
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId(){
		Lancamento lancamento = this.criarMassa();
		this.repository.save(lancamento);
		
		Optional<Lancamento> lancamentoBanco = this.repository.findById(lancamento.getId());
		
		Assertions.assertThat(lancamentoBanco.isPresent()).isTrue();
	}
	
	
	
	public static Lancamento criarMassa() {
		Lancamento lancamento = Lancamento.builder()
								.ano(2022)
								.mes(1)
								.descricao("Lancamento qualquer")
								.valor(BigDecimal.valueOf(10))
								.tipo(TipoLancamento.RECEITA)
								.status(StatusLancamento.PENDENTE)
								.dataCadastro(LocalDate.now())
								.build();
		return lancamento;
	}
	
}
