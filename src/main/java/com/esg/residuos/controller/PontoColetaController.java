package com.esg.residuos.controller;

import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.request.PontoColetaRequest;
import com.esg.residuos.dto.response.DescarteResponse;
import com.esg.residuos.dto.response.PontoColetaResponse;
import com.esg.residuos.service.DescarteService;
import com.esg.residuos.service.PontoColetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pontos-coleta")
@RequiredArgsConstructor
@Tag(name = "Pontos de Coleta", description = "Locais de recebimento de residuos e monitoramento de ocupacao")
public class PontoColetaController {

    private final PontoColetaService service;
    private final DescarteService descarteService;

    @GetMapping
    public ResponseEntity<List<PontoColetaResponse>> listar(
            @RequestParam(required = false) StatusPonto status) {
        if (status != null) {
            return ResponseEntity.ok(service.listarPorStatus(status));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/{id}/historico-descartes")
    public ResponseEntity<List<DescarteResponse>> historicoDescartes(@PathVariable Long id) {
        return ResponseEntity.ok(descarteService.historicoPorPonto(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PontoColetaResponse criar(@Valid @RequestBody PontoColetaRequest request) {
        return service.criar(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody PontoColetaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
