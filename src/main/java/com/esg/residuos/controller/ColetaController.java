package com.esg.residuos.controller;

import com.esg.residuos.dto.request.ColetaRequest;
import com.esg.residuos.dto.request.ColetaUpdateRequest;
import com.esg.residuos.dto.response.ColetaResponse;
import com.esg.residuos.service.ColetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coletas")
@RequiredArgsConstructor
@Tag(name = "Coletas", description = "Agendamento, acompanhamento e conclusao das coletas de residuos")
public class ColetaController {

    private final ColetaService service;

    @GetMapping
    public ResponseEntity<List<ColetaResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColetaResponse agendar(@Valid @RequestBody ColetaRequest request) {
        return service.agendar(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColetaResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ColetaUpdateRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable Long id) {
        service.cancelar(id);
    }
}
