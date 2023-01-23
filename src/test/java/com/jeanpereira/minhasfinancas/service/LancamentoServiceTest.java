package com.jeanpereira.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.jeanpereira.minhasfinancas.model.entity.Lancamento;
import com.jeanpereira.minhasfinancas.model.entity.Usuario;
import com.jeanpereira.minhasfinancas.model.enums.StatusLancamento;
import com.jeanpereira.minhasfinancas.model.enums.TipoLancamento;
import com.jeanpereira.minhasfinancas.model.repository.LancamentoRepository;
import com.jeanpereira.minhasfinancas.model.repository.UsuarioRepository;



@SpringBootTest
@RunWith(SpringRunner.class)
class LancamentoServiceTest {
	
	private String nome = "teste";
	private String email = "teste@teste";
	private String senha = "teste";
	
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	LancamentoService lancamentoService;
	@Autowired
	LancamentoRepository lancamentoRepository;
	
	@AfterEach
	@BeforeEach
	private void setUp() {
		
		lancamentoRepository.deleteAll();
		usuarioRepository.deleteAll();
	}

	@Test
	public void deveSalvarUmLancamento() {
		Usuario usuarioASalvar = criarUsuario();
		Usuario usuario = usuarioService.salvarUsuario(usuarioASalvar);
		
		Lancamento lancamentoASalvar = criarLancamento(usuario);
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamentoASalvar);
		
		Optional<Lancamento> lancamento = lancamentoService.obterPorId(lancamentoSalvo.getId());
		
		Assertions.assertThat(lancamento.get().getId()).isEqualTo(lancamentoSalvo.getId());
	}
	
//	@Test
//	public void naoDeveSalvarUmLancamentoQuandoOuverErroDeValidacao() {
//		fail("Not yet implemented");
//	}
	
	private Lancamento criarLancamento(Usuario usuario) {
		return Lancamento.builder()
				.descricao("Lancamento de teste")
				.mes(1)
				.ano(2025)
				.valor(BigDecimal.valueOf(300))
				.dataCadastro(LocalDate.now())
				.usuario(usuario)
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.build();
	}
	
	private Usuario criarUsuario(){
		return Usuario.builder()
				.nome(nome)
				.email(email)
				.senha(senha)
				.build();
	}

}
