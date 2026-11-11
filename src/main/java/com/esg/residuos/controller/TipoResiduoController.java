package com.esg.residuos.controller;

import com.esg.residuos.dto.request.TipoResiduoRequest;
import com.esg.residuos.dto.response.TipoResiduoResponse;
import com.esg.residuos.service.TipoResiduoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-residuo")
@RequiredArgsConstructor
@Tag(name = "Tipos de Residuo", description = "Cadastro dos tipos de residuo controlados pelo sistema")
public class TipoResiduoController {

    private final TipoResiduoService service;

    @GetMapping
    public ResponseEntity<List<TipoResiduoResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoResiduoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoResiduoResponse criar(@Valid @RequestBody TipoResiduoRequest request) {
        return service.criar(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoResiduoResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody TipoResiduoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
