package com.esg.residuos.controller;

import com.esg.residuos.dto.request.ProdutoRequest;
import com.esg.residuos.dto.response.ProdutoResponse;
import com.esg.residuos.dto.response.TransacaoResponse;
import com.esg.residuos.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    @GetMapping
    public List<ProdutoResponse> listar() {
        return service.listar();
    }

    /** O app troca a URL (?v=) quando o produto e editado, entao a foto pode ficar em cache. */
    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> imagem(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePrivate())
                .body(service.imagem(id));
    }

    /** Apenas ARTESAO (SecurityConfig); o produto vai para o atelie de quem esta logado. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@Valid @RequestBody ProdutoRequest request, Principal principal) {
        return service.criar(principal.getName(), request);
    }

    /** Apenas o artesao dono (checado em ProdutoService). */
    @PutMapping("/{id}")
    public ProdutoResponse editar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request, Principal principal) {
        return service.editar(id, principal.getName(), request);
    }

    /** Artesao dono ou ADMIN. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, Authentication auth) {
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        service.excluir(id, auth.getName(), admin);
    }

    /** Qualquer usuario logado compra com EcoMoedas; devolve o codigo de retirada. */
    @PostMapping("/{id}/compras")
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponse comprar(@PathVariable Long id, Principal principal) {
        return service.comprar(id, principal.getName());
    }
}
