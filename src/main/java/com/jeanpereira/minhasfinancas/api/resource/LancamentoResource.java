package com.jeanpereira.minhasfinancas.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanpereira.minhasfinancas.api.dto.LancamentoDTO;
import com.jeanpereira.minhasfinancas.service.LancamentoService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {
	
	private LancamentoService service;

	public LancamentoResource(LancamentoService service) {
		super();
		this.service = service;
	}
	
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		
	}

}
