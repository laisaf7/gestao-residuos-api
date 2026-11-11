package com.esg.residuos.controller;

import com.esg.residuos.dto.response.IndicadoresResponse;
import com.esg.residuos.service.IndicadorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/indicadores-sustentabilidade")
@RequiredArgsConstructor
@Tag(name = "Indicadores de Sustentabilidade", description = "Indicadores ESG consolidados do sistema de gestao de residuos")
public class IndicadorController {

    private final IndicadorService service;

    @GetMapping
    public ResponseEntity<IndicadoresResponse> gerar() {
        return ResponseEntity.ok(service.gerar());
    }
}
