package com.esg.residuos.controller;

import com.esg.residuos.dto.request.EntregaQrRequest;
import com.esg.residuos.dto.request.PagamentoRequest;
import com.esg.residuos.dto.request.ResgateRequest;
import com.esg.residuos.dto.response.CarteiraResponse;
import com.esg.residuos.dto.response.ComercioResponse;
import com.esg.residuos.dto.response.EntregaQrResponse;
import com.esg.residuos.dto.response.ResgateResponse;
import com.esg.residuos.dto.response.TransacaoResponse;
import com.esg.residuos.service.CarteiraService;
import com.esg.residuos.service.ComercioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CarteiraController {

    private final CarteiraService carteiraService;
    private final ComercioService comercioService;

    @GetMapping("/carteira")
    public CarteiraResponse consultar(Principal principal) {
        return carteiraService.consultar(principal.getName());
    }

    @PostMapping("/carteira/resgates")
    @ResponseStatus(HttpStatus.CREATED)
    public ResgateResponse resgatar(@Valid @RequestBody ResgateRequest request, Principal principal) {
        return carteiraService.resgatar(principal.getName(), request.getCodigo());
    }

    @PostMapping("/carteira/pagamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponse pagar(@Valid @RequestBody PagamentoRequest request, Principal principal) {
        return carteiraService.pagar(principal.getName(), request);
    }

    /** Apenas ADMIN (operador do ecoponto), pela regra geral de POST em SecurityConfig. */
    @PostMapping("/entregas-qr")
    @ResponseStatus(HttpStatus.CREATED)
    public EntregaQrResponse gerarQr(@Valid @RequestBody EntregaQrRequest request) {
        return carteiraService.gerarQr(request);
    }

    @GetMapping("/comercios")
    public List<ComercioResponse> listarComercios(@RequestParam(required = false) String categoria) {
        return comercioService.listar(categoria);
    }
}
