package com.esg.residuos.service;

import com.esg.residuos.domain.entity.Notificacao;
import com.esg.residuos.dto.response.NotificacaoResponse;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificacaoService {

    private final NotificacaoRepository repository;

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listarTodas() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listarNaoLidas() {
        return repository.findByLida(Boolean.FALSE).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NotificacaoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    private Notificacao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacao nao encontrada com id " + id));
    }

    public NotificacaoResponse marcarComoLida(Long id) {
        Notificacao notificacao = buscarEntidade(id);
        notificacao.setLida(Boolean.TRUE);
        return toResponse(repository.save(notificacao));
    }

    private NotificacaoResponse toResponse(Notificacao n) {
        return NotificacaoResponse.builder()
                .id(n.getId())
                .idPontoColeta(n.getPontoColeta().getId())
                .nomePontoColeta(n.getPontoColeta().getNome())
                .idColeta(n.getColeta() != null ? n.getColeta().getId() : null)
                .tipo(n.getTipo())
                .titulo(n.getTitulo())
                .mensagem(n.getMensagem())
                .dataNotificacao(n.getDataNotificacao())
                .lida(n.getLida())
                .prioridade(n.getPrioridade())
                .build();
    }
}
