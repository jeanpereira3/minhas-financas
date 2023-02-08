package com.jeanpereira.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanpereira.minhasfinancas.exeption.ErroDeAutenticacaoExeption;
import com.jeanpereira.minhasfinancas.exeption.RegraDeNegocioExeption;
import com.jeanpereira.minhasfinancas.model.entity.Usuario;
import com.jeanpereira.minhasfinancas.model.repository.UsuarioRepository;
import com.jeanpereira.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private UsuarioRepository repository;
	
	private PasswordEncoder encoder;

	public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		if(!usuario.isPresent()) {
			throw new ErroDeAutenticacaoExeption("Usuario não encontrado.");
		}
		
		boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());
		
		if (!senhasBatem) {
			throw new ErroDeAutenticacaoExeption("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		
		criptografarSenha(usuario);
		
		return repository.save(usuario);
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaCodificada = encoder.encode(senha);
		usuario.setSenha(senhaCodificada);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraDeNegocioExeption("Já existe um usuário cadastrado com esse email.");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}

}
