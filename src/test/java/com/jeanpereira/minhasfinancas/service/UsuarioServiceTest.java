package com.jeanpereira.minhasfinancas.service;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.jeanpereira.minhasfinancas.model.entity.Usuario;
import com.jeanpereira.minhasfinancas.model.repository.UsuarioRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
class UsuarioServiceTest {
	
	private String nome = "teste";
	private String email = "teste@teste";
	private String senha = "teste";
	private String senhaInvalida = "senhaInalida";
	
	@Autowired
	UsuarioService service;
	
	@Autowired
	UsuarioRepository repository;
	
	@AfterEach
	@BeforeEach
	private void setUp() {
		repository.deleteAll();
	}

	@Test
	public void deveValidarEmail() {	
		boolean exption = false;
			
		try {
			service.validarEmail(email);
		} catch (Exception e) {
				exption = true;
		}

		Assertions.assertThat(exption).isFalse();		
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		buildUsuario();
			
		Exception exception = null;
			
		try {
			service.validarEmail(email);
		} catch (Exception e) {
			exception = e;
		}
			
		Assert.assertEquals("Já existe um usuário cadastrado com esse email.", exception.getMessage());		
	}
	
	@Test
	public void deveRetornarErroQuandoEmailNaoCadastrado() {
		Exception exception = null;
		
		try {
			service.autenticar(email, senha);
		} catch (Exception e) {
			exception = e;
		}
		
		Assert.assertEquals("Usuario não encontrado.", exception.getMessage());
		
	}
	
	@Test
	public void deveRetornarErroQuandoEmailCorretoESenhaInvalido() {
		buildUsuario();
		Exception exception = null;
		
		try {
			service.autenticar(email, senhaInvalida);
		} catch (Exception e) {
			exception = e;
		}
		
		Assert.assertEquals("Senha inválida.", exception.getMessage());
		
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		buildUsuario();
		
		Usuario resultado = service.autenticar(email, senha);

		Assertions.assertThat(resultado).isNotNull();
		
	}
	
	@Test
	public void naoDeveSalvarUsuarioComEmailJaCadastrado(){
		buildUsuario();
		
		Exception exception = null;
		Usuario usuario = Usuario.builder().nome(nome).email(email).senha(senha).build();
		try {
			service.salvarUsuario(usuario);
		} catch (Exception e) {
			exception = e;
		}
		
		Assert.assertEquals("Já existe um usuário cadastrado com esse email.", exception.getMessage());
	}
	
	@Test
	public void daveSalvarUsuario() {
		Usuario usuario = Usuario.builder().nome(nome).email(email).senha(senha).build();
		Usuario usuarioSalvo = service.salvarUsuario(usuario);
		
		Assert.assertEquals(nome, usuarioSalvo.getNome());
		Assert.assertEquals(email, usuarioSalvo.getEmail());
		Assert.assertEquals(senha, usuarioSalvo.getSenha());    
	}
	
	private void buildUsuario(){
		Usuario usuario = Usuario.builder().nome(nome).email(email).senha(senha).build();
		repository.save(usuario);
	}

}
