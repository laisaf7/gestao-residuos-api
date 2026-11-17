package com.esg.residuos.controller;

import com.esg.residuos.dto.request.PontoColetaRequest;
import com.esg.residuos.dto.response.LoginResponse;
import com.esg.residuos.dto.response.PontoColetaResponse;
import com.esg.residuos.security.CustomUserDetailsService;
import com.esg.residuos.security.JwtService;
import com.esg.residuos.service.ArtesaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/artesao")
@RequiredArgsConstructor
public class ArtesaoController {

    private final ArtesaoService service;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    /** Usuario comum vira artesao. Devolve a sessao nova (papel ARTESAO) para o app nao pedir login de novo. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse cadastrar(@Valid @RequestBody PontoColetaRequest request, Principal principal) {
        service.cadastrar(principal.getName(), request);
        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(principal.getName()), "ARTESAO");
        return new LoginResponse(token, "Bearer", principal.getName(), "ARTESAO");
    }

    @GetMapping("/atelie")
    public PontoColetaResponse meuAtelie(Principal principal) {
        return service.meuAtelie(principal.getName());
    }

    @PostMapping("/atelie/esvaziar")
    public PontoColetaResponse esvaziar(Principal principal) {
        return service.esvaziar(principal.getName());
    }
}
