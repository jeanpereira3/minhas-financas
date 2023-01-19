package com.jeanpereira.minhasfinancas.exeption;

public class ErroDeAutenticacaoExeption extends RuntimeException {
	
	public ErroDeAutenticacaoExeption(String msg) {
		super(msg);
	}
}
