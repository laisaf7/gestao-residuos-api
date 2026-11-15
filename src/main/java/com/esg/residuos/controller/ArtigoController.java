package com.esg.residuos.controller;

import com.esg.residuos.dto.request.ArtigoRequest;
import com.esg.residuos.dto.response.ArtigoResponse;
import com.esg.residuos.service.ArtigoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/artigos")
@RequiredArgsConstructor
public class ArtigoController {

    private final ArtigoService service;

    @GetMapping
    public List<ArtigoResponse> listar(@RequestParam(required = false) String material) {
        return service.listar(material);
    }

    /** Qualquer usuario autenticado pode publicar (liberado em SecurityConfig). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtigoResponse criar(@Valid @RequestBody ArtigoRequest request, Principal principal) {
        return service.criar(principal.getName(), request);
    }
}
