package com.jeanpereira.minhasfinancas.exeption;

public class RegraDeNegocioExeption extends RuntimeException {
	public RegraDeNegocioExeption(String smg) {
		super(smg);
	}
}
