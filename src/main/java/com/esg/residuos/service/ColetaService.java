package com.esg.residuos.service;

import com.esg.residuos.domain.entity.Coleta;
import com.esg.residuos.domain.entity.Notificacao;
import com.esg.residuos.domain.entity.PontoColeta;
import com.esg.residuos.domain.enums.Prioridade;
import com.esg.residuos.domain.enums.StatusColeta;
import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.request.ColetaRequest;
import com.esg.residuos.dto.request.ColetaUpdateRequest;
import com.esg.residuos.dto.response.ColetaResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.ColetaRepository;
import com.esg.residuos.repository.NotificacaoRepository;
import com.esg.residuos.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negocio do agendamento e ciclo de vida das coletas.
 * A transicao de status para CONCLUIDA reproduz a Atividade 4
 * (TRG_FINALIZAR_COLETA) do projeto original em PL/SQL.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ColetaService {

    private final ColetaRepository coletaRepository;
    private final PontoColetaRepository pontoColetaRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final PontoColetaService pontoColetaService;

    @Transactional(readOnly = true)
    public List<ColetaResponse> listarTodos() {
        return coletaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ColetaResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    private Coleta buscarEntidade(Long id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coleta nao encontrada com id " + id));
    }

    /** Agendamento manual de uma nova coleta para um ponto. */
    public ColetaResponse agendar(ColetaRequest request) {
        PontoColeta ponto = pontoColetaService.buscarEntidade(request.getIdPontoColeta());
        Coleta coleta = Coleta.builder()
                .pontoColeta(ponto)
                .dataSolicitacao(LocalDate.now())
                .dataAgendada(request.getDataAgendada())
                .status(request.getDataAgendada() != null ? StatusColeta.AGENDADA : StatusColeta.SOLICITADA)
                .destinoFinal(request.getDestinoFinal())
                .observacao(request.getObservacao())
                .build();
        return toResponse(coletaRepository.save(coleta));
    }

    /** Atualiza uma coleta existente; ao mudar o status para CONCLUIDA, finaliza o ciclo do ponto. */
    public ColetaResponse atualizar(Long id, ColetaUpdateRequest request) {
        Coleta coleta = buscarEntidade(id);

        if (coleta.getStatus() == StatusColeta.CONCLUIDA || coleta.getStatus() == StatusColeta.CANCELADA) {
            throw new BusinessException("Nao e possivel atualizar uma coleta que ja esta " + coleta.getStatus());
        }

        if (request.getDataAgendada() != null) {
            coleta.setDataAgendada(request.getDataAgendada());
        }
        if (request.getDestinoFinal() != null) {
            coleta.setDestinoFinal(request.getDestinoFinal());
        }
        if (request.getObservacao() != null) {
            coleta.setObservacao(request.getObservacao());
        }

        if (request.getStatus() != null && request.getStatus() != coleta.getStatus()) {
            if (request.getStatus() == StatusColeta.CONCLUIDA) {
                finalizarColeta(coleta);
            } else {
                coleta.setStatus(request.getStatus());
            }
        }

        return toResponse(coletaRepository.save(coleta));
    }

    /** Cancela (nao exclui fisicamente) uma solicitacao de coleta em aberto. */
    public void cancelar(Long id) {
        Coleta coleta = buscarEntidade(id);
        if (coleta.getStatus() == StatusColeta.CONCLUIDA) {
            throw new BusinessException("Nao e possivel cancelar uma coleta ja concluida");
        }
        coleta.setStatus(StatusColeta.CANCELADA);
        coletaRepository.save(coleta);
    }

    /** Atividade 4: fecha o ciclo de vida do residuo no ponto de coleta. */
    private void finalizarColeta(Coleta coleta) {
        PontoColeta ponto = coleta.getPontoColeta();
        BigDecimal quantidadeColetada = ponto.getQuantidadeAtualKg();

        coleta.setStatus(StatusColeta.CONCLUIDA);
        coleta.setDataColeta(LocalDate.now());
        coleta.setQuantidadeColetadaKg(quantidadeColetada);

        ponto.setQuantidadeAtualKg(BigDecimal.ZERO);
        ponto.setPercentualOcupacao(BigDecimal.ZERO);
        ponto.setStatus(StatusPonto.DISPONIVEL);
        ponto.setDataUltimaColeta(LocalDate.now());
        pontoColetaRepository.save(ponto);

        Notificacao notificacao = Notificacao.builder()
                .pontoColeta(ponto)
                .coleta(coleta)
                .tipo("COLETA_CONCLUIDA")
                .titulo("Coleta concluida")
                .mensagem("A coleta do ponto '" + ponto.getNome()
                        + "' foi concluida e o ponto esta pronto para um novo ciclo.")
                .prioridade(Prioridade.MEDIA)
                .build();
        notificacaoRepository.save(notificacao);
    }

    private ColetaResponse toResponse(Coleta c) {
        return ColetaResponse.builder()
                .id(c.getId())
                .idPontoColeta(c.getPontoColeta().getId())
                .nomePontoColeta(c.getPontoColeta().getNome())
                .dataSolicitacao(c.getDataSolicitacao())
                .dataAgendada(c.getDataAgendada())
                .dataColeta(c.getDataColeta())
                .quantidadeColetadaKg(c.getQuantidadeColetadaKg())
                .status(c.getStatus())
                .destinoFinal(c.getDestinoFinal())
                .observacao(c.getObservacao())
                .build();
    }
}
