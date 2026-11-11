package com.esg.residuos.controller;

import com.esg.residuos.dto.response.NotificacaoResponse;
import com.esg.residuos.service.NotificacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
@Tag(name = "Notificacoes", description = "Alertas automaticos sobre descarte incorreto, limite de coleta e conclusao de ciclo")
public class NotificacaoController {

    private final NotificacaoService service;

    @GetMapping
    public ResponseEntity<List<NotificacaoResponse>> listar(
            @RequestParam(required = false) Boolean naoLidas) {
        if (Boolean.TRUE.equals(naoLidas)) {
            return ResponseEntity.ok(service.listarNaoLidas());
        }
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}/marcar-lida")
    public ResponseEntity<NotificacaoResponse> marcarComoLida(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarComoLida(id));
    }
}
