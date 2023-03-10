package com.jeanpereira.minhasfinancas.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jeanpereira.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.jeanpereira.minhasfinancas.api.dto.LancamentoDTO;
import com.jeanpereira.minhasfinancas.exeption.RegraDeNegocioExeption;
import com.jeanpereira.minhasfinancas.model.entity.Lancamento;
import com.jeanpereira.minhasfinancas.model.entity.Usuario;
import com.jeanpereira.minhasfinancas.model.enums.StatusLancamento;
import com.jeanpereira.minhasfinancas.model.enums.TipoLancamento;
import com.jeanpereira.minhasfinancas.service.LancamentoService;
import com.jeanpereira.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;

	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraDeNegocioExeption e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}	
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.obterPorId(id).map(entity ->{
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraDeNegocioExeption e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet(
				() -> new ResponseEntity(
						"Lancamento n??o encontrado na base de dados.", HttpStatus.BAD_REQUEST
						));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id ,@RequestBody AtualizaStatusDTO dto) {
		return service.obterPorId(id).map(entity ->{
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			if (statusSelecionado == null) {
				return ResponseEntity
						.badRequest()
						.body("N??o foi poss??vel atualizar o status do lan??amento, envie um status v??lido");
			}
			
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraDeNegocioExeption e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
			
		}).orElseGet(
				() -> new ResponseEntity(
						"Lancamento n??o encontrado na base de dados.", HttpStatus.BAD_REQUEST
						));
	}
	
	@GetMapping("{id}")
	public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
		return service.obterPorId(id)
				.map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping
	public ResponseEntity buscar(
		@RequestParam(value = "descricao", required = false) String descricao,
		@RequestParam(value = "mes", required = false) Integer mes,
		@RequestParam(value = "ano", required = false) Integer ano,
		@RequestParam("usuario") Long idUsuario
		) {
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("N??o foi possivel realizar consulta. Usu??rio n??o encontrado.");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entity -> {
			service.deletar(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(()-> new ResponseEntity(
				"Lancamento n??o encontrado na base de dados.", HttpStatus.BAD_REQUEST
				));
	}
	
	private LancamentoDTO converter(Lancamento lancamento) {
		return LancamentoDTO.builder()
				.id(lancamento.getId())
				.descricao(lancamento.getDescricao())
				.mes(lancamento.getMes())
				.ano(lancamento.getAno())
				.valor(lancamento.getValor())
				.status(lancamento.getStatus().name())
				.tipo(lancamento.getTipo().name())
				.usuario(lancamento.getUsuario().getId())
				.build();
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
		.orElseThrow(()->
			new RegraDeNegocioExeption("Usuario n??o encontrado para o Id informado.")
		);
		
		lancamento.setUsuario(usuario);
		
		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		return lancamento;
	}

}
