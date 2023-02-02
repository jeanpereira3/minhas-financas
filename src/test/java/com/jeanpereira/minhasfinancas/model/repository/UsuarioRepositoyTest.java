package com.jeanpereira.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jeanpereira.minhasfinancas.model.entity.Usuario;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UsuarioRepositoyTest {
	
	private String nome = "teste";
	private String email = "teste@teste";

	@Autowired
	UsuarioRepository repository;
	
	@BeforeEach
	@AfterEach
	public void setUp() {
		repository.deleteAll();
	}
	
	@Test
	public void deveVerificarAExisteciaDeUmEmail() {
		
		Usuario usuario = Usuario.builder().nome(nome).email(email).build();
		repository.save(usuario);

		boolean resultado = repository.existsByEmail(email);
		
		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoOuverEmailCadastrado() {
		
		boolean resultado = repository.existsByEmail(email);
		
		Assertions.assertThat(resultado).isFalse();
	}
	
	@Test
	public void deveBuscarUsuarioPorEmail() {
		Usuario usuario = Usuario.builder().nome(nome).email(email).build();
		repository.save(usuario);
		
		Optional<Usuario> resultado = repository.findByEmail(email);
		
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoAoBuscarUsuarioPorEmailQuandoNaoExistirNaBase() {
		
		Optional<Usuario> resultado = repository.findByEmail(email);
		
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
}
