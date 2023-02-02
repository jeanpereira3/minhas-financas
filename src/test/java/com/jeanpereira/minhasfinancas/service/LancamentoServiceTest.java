package com.jeanpereira.minhasfinancas.service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jeanpereira.minhasfinancas.exeption.RegraDeNegocioExeption;
import com.jeanpereira.minhasfinancas.model.entity.Lancamento;
import com.jeanpereira.minhasfinancas.model.entity.Usuario;
import com.jeanpereira.minhasfinancas.model.enums.StatusLancamento;
import com.jeanpereira.minhasfinancas.model.enums.TipoLancamento;
import com.jeanpereira.minhasfinancas.model.repository.LancamentoRepository;
import com.jeanpereira.minhasfinancas.model.repository.UsuarioRepository;



@SpringBootTest
@ExtendWith(SpringExtension.class)
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
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoOuverErroDeValidacao() {	
		Exception exception = null;
		
		try {
			Lancamento lancamentoASalvar = criarLancamento(null);
			Lancamento lancamentoSalvo = lancamentoService.salvar(lancamentoASalvar);
		} catch (Exception e) {
			exception = e;
		}
		
		Assertions.assertThat(exception.getMessage()).isEqualTo("Informe um Usuário.");
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {	
		Usuario usuarioASalvar = criarUsuario();
		Usuario usuario = usuarioService.salvarUsuario(usuarioASalvar);
		
		Lancamento lancamentoASalvar = criarLancamento(usuario);
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamentoASalvar);
		
		lancamentoSalvo.setDescricao("Lancamento atualizado.");
		Lancamento lancamentoAtualizado = lancamentoService.atualizar(lancamentoSalvo);
		
		Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Lancamento atualizado.");
		Assertions.assertThat(lancamentoSalvo.getId()).isEqualTo(lancamentoAtualizado.getId());
		
	}
	
	@Test
	public void deveGerarErroAoAtualizarLancamentoInexistente() {	
		Usuario usuarioASalvar = criarUsuario();
		Usuario usuario = usuarioService.salvarUsuario(usuarioASalvar);
		
		Lancamento lancamento = criarLancamento(usuario);
		
		Assertions.catchThrowableOfType(() -> lancamentoService.salvar(lancamento), NullPointerException.class);	
	}
	
	@Test
	public void deveDeletarUnLancamento() {
		Usuario usuarioASalvar = criarUsuario();
		Usuario usuario = usuarioService.salvarUsuario(usuarioASalvar);
		
		Lancamento lancamentoASalvar = criarLancamento(usuario);
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamentoASalvar);
		
		lancamentoService.deletar(lancamentoSalvo);
		
		Optional<Lancamento> lancamentoDeletado = lancamentoService.obterPorId(lancamentoSalvo.getId());
		
		Assertions.assertThat(lancamentoDeletado).isEmpty();
			
	}
	
	@Test
	public void naoDeveDeletarUnLancamentoInexistente() {
		Usuario usuarioASalvar = criarUsuario();
		
		Lancamento lancamentoASalvar = criarLancamento(usuarioASalvar);
		
		Assertions.catchThrowableOfType(
				()-> lancamentoService.deletar(lancamentoASalvar),
				NullPointerException.class);
		
		
		
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		Usuario usuarioASalvar = criarUsuario();
		Usuario usuario = usuarioService.salvarUsuario(usuarioASalvar);
		
		Lancamento lancamentoASalvar = criarLancamento(usuario);
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamentoASalvar);
		
		List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoSalvo);
		
		Assertions.assertThat(lancamentos)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamentoSalvo);
			
	}
	
	@Test
	public void deveAtualizarStatusLancamento() {
		Usuario usuarioASalvar = criarUsuario();
		Usuario usuario = usuarioService.salvarUsuario(usuarioASalvar);
		
		Lancamento lancamentoASalvar = criarLancamento(usuario);
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamentoASalvar);
		
		lancamentoService.atualizarStatus(lancamentoSalvo, StatusLancamento.CANCELADO);
		
		Optional<Lancamento> lancamentoAtualizado = lancamentoService.obterPorId(lancamentoSalvo.getId());
		
		Assertions.assertThat(lancamentoAtualizado.get().getStatus())
			.isEqualTo(StatusLancamento.CANCELADO);
	}
	
	@Test
	public void develancarErrosAoValidarLancamento() {
		Lancamento lancamento = new Lancamento();
		Usuario usuario = criarUsuario();
		
		Throwable erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("Descricao teste validação.");
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(12);
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Ano válido");
		
		lancamento.setAno(999);
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Ano válido");
		
		lancamento.setAno(20222);
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Ano válido");
		
		lancamento.setAno(2022);
		
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(usuario);
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Usuário.");
		usuario.setId(1l);
		lancamento.setUsuario(usuario);
		
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Valor válido");
		
		lancamento.setValor(BigDecimal.valueOf(0));
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Valor válido");
		
		lancamento.setValor(BigDecimal.valueOf(300));
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro)
			.isInstanceOf(RegraDeNegocioExeption.class)
			.hasMessage("Informe um Tipo de lancamento.");
		
		lancamento.setTipo(TipoLancamento.RECEITA);
		
		erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
		Assertions.assertThat(erro).isNull();;
	}
	
	private Lancamento criarLancamento(Usuario usuario) {
		return Lancamento.builder()
				.descricao("Lancamento de teste")
				.mes(1)
				.ano(2025)
				.valor(BigDecimal.valueOf(300.99))
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
