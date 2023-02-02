package com.jeanpereira.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jeanpereira.minhasfinancas.model.entity.Lancamento;
import com.jeanpereira.minhasfinancas.model.enums.StatusLancamento;
import com.jeanpereira.minhasfinancas.model.enums.TipoLancamento;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;

	@BeforeEach
	@AfterEach
	private void setUp() {
		repository.deleteAll();
	}

	@Test
	public void deveSalvarLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveDeletarLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		
		Optional<Lancamento> lancamentoSelecionado = repository.findById(lancamento.getId());
		repository.delete(lancamentoSelecionado.get());
		Optional<Lancamento> lancamentoExcluido = repository.findById(lancamento.getId());
		
		Assertions.assertThat(lancamentoExcluido).isEmpty();
	}
	
	@Test
	public void deveAtualizarLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		
		lancamento.setDescricao("Lancamento Atualizado");
		repository.save(lancamento);
		
		Optional<Lancamento> lancamentoAtualizado = repository.findById(lancamento.getId());
		
		Assertions.assertThat(lancamentoAtualizado.get().getDescricao()).contains("Lancamento Atualizado");
	}
	
	private Lancamento criarLancamento() {
		return Lancamento.builder()
				.descricao("Lancamento de teste")
				.mes(1)
				.ano(2025)
				.valor(BigDecimal.valueOf(300))
				.dataCadastro(LocalDate.now())
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.build();
	}

}
