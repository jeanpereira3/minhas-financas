package com.jeanpereira.minhasfinancas.api.resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeanpereira.minhasfinancas.api.dto.UsuarioDTO;
import com.jeanpereira.minhasfinancas.exeption.ErroDeAutenticacaoExeption;
import com.jeanpereira.minhasfinancas.model.entity.Usuario;
import com.jeanpereira.minhasfinancas.service.LancamentoService;
import com.jeanpereira.minhasfinancas.service.UsuarioService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
class UsuarioResourceTest {
	
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	private String email = "teste@teste.com";
	private String senha = "123456";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService usuarioService;
	
	@MockBean
	LancamentoService lancamentoService;

	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Usuario usuario = Usuario.builder()
				.nome("teste")
				.email(email)
				.senha(senha)
				.build();
		Mockito.when(usuarioService.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API.concat("/autenticar"))
				.accept(JSON)
				.contentType(JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("senha").value(usuario.getSenha()));
		
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarAutenticarUsuarioInexistente() throws Exception {
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(usuarioService.autenticar(email, senha)).thenThrow(ErroDeAutenticacaoExeption.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API.concat("/autenticar"))
				.accept(JSON)
				.contentType(JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}
	
	@Test
	public void deveSalvarUsuario() throws Exception {
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Usuario usuario = Usuario.builder()
				.nome("teste")
				.email(email)
				.senha(senha)
				.build();
		
		Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API)
				.accept(JSON)
				.contentType(JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()))
			.andExpect(MockMvcResultMatchers.jsonPath("senha").value(usuario.getSenha()));;
		
	}

}
