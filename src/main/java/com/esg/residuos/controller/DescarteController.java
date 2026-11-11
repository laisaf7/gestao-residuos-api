package com.esg.residuos.controller;

import com.esg.residuos.dto.request.DescarteRequest;
import com.esg.residuos.dto.response.DescarteResponse;
import com.esg.residuos.service.DescarteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/descartes")
@RequiredArgsConstructor
@Tag(name = "Descartes", description = "Registro e rastreamento do descarte de residuos nos pontos de coleta")
public class DescarteController {

    private final DescarteService service;

    @GetMapping
    public ResponseEntity<List<DescarteResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DescarteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DescarteResponse registrar(@Valid @RequestBody DescarteRequest request) {
        return service.registrar(request);
    }
}
