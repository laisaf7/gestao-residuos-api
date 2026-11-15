package com.esg.residuos.controller;

import com.esg.residuos.dto.request.ArtigoRequest;
import com.esg.residuos.dto.response.ArtigoResponse;
import com.esg.residuos.service.ArtigoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
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

    /** Artigos nao sao editados, entao a foto pode ficar em cache no celular. */
    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> imagem(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePrivate())
                .body(service.imagem(id));
    }

    /** Qualquer usuario autenticado pode publicar (liberado em SecurityConfig). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtigoResponse criar(@Valid @RequestBody ArtigoRequest request, Principal principal) {
        return service.criar(principal.getName(), request);
    }

    /** Apenas quem publicou (checado em ArtigoService). */
    @PutMapping("/{id}")
    public ArtigoResponse editar(@PathVariable Long id, @Valid @RequestBody ArtigoRequest request, Principal principal) {
        return service.editar(id, principal.getName(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, Principal principal) {
        service.excluir(id, principal.getName());
    }
}
