package com.jeanpereira.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.jeanpereira.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);
	

}
