package com.esg.residuos.service;

import com.esg.residuos.domain.entity.*;
import com.esg.residuos.domain.enums.Prioridade;
import com.esg.residuos.domain.enums.StatusColeta;
import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.request.DescarteRequest;
import com.esg.residuos.dto.response.DescarteResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.ColetaRepository;
import com.esg.residuos.repository.DescarteRepository;
import com.esg.residuos.repository.NotificacaoRepository;
import com.esg.residuos.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negocio do registro de descarte de residuos.
 * <p>
 * Esta classe reproduz, na camada de aplicacao, a automacao originalmente
 * definida nas triggers PL/SQL do projeto (TRG_VALIDAR_DESCARTE,
 * TRG_ATUALIZAR_PONTO_COLETA e TRG_GERAR_COLETA), garantindo o mesmo
 * comportamento mesmo quando o banco de dados nao disponibiliza as triggers.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DescarteService {

    private final DescarteRepository descarteRepository;
    private final PontoColetaRepository pontoColetaRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final ColetaRepository coletaRepository;
    private final TipoResiduoService tipoResiduoService;
    private final PontoColetaService pontoColetaService;

    @Transactional(readOnly = true)
    public List<DescarteResponse> listarTodos() {
        return descarteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DescarteResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<DescarteResponse> historicoPorPonto(Long pontoColetaId) {
        pontoColetaService.buscarEntidade(pontoColetaId);
        return descarteRepository.findByPontoColetaIdOrderByDataDescarteDesc(pontoColetaId)
                .stream().map(this::toResponse).toList();
    }

    private Descarte buscarEntidade(Long id) {
        return descarteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Descarte nao encontrado com id " + id));
    }

    /** Atividade 1, 2 e 3 do projeto: valida, registra, atualiza o ponto e gera coleta automaticamente. */
    public DescarteResponse registrar(DescarteRequest request) {
        return registrar(request, null);
    }

    /** Variante usada no resgate de QR Code, que vincula o descarte ao cidadao. */
    public DescarteResponse registrar(DescarteRequest request, Usuario usuario) {
        PontoColeta ponto = pontoColetaRepository.findById(request.getIdPontoColeta())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ponto de coleta nao encontrado com id " + request.getIdPontoColeta()));
        TipoResiduo tipoInformado = tipoResiduoService.buscarEntidade(request.getIdTipoResiduo());

        // Atividade 1: bloqueia o descarte se o ponto estiver inativo
        if (!Boolean.TRUE.equals(ponto.getAtivo()) || ponto.getStatus() == StatusPonto.INATIVO) {
            throw new BusinessException("Nao e possivel registrar descarte: o ponto de coleta '"
                    + ponto.getNome() + "' esta inativo");
        }

        boolean descarteCorreto = ponto.getTipoResiduo().getId().equals(tipoInformado.getId());

        String observacao = null;
        if (!descarteCorreto) {
            observacao = "Descarte incorreto. Destinacao correta para " + tipoInformado.getNome()
                    + ": " + tipoInformado.getDestinacaoCorreta();
        }

        Descarte descarte = Descarte.builder()
                .pontoColeta(ponto)
                .tipoResiduo(tipoInformado)
                .dataDescarte(LocalDate.now())
                .quantidadeKg(request.getQuantidadeKg())
                .origem(request.getOrigem())
                .usuario(usuario)
                .descarteCorreto(descarteCorreto)
                .observacao(observacao)
                .build();
        descarte = descarteRepository.save(descarte);

        if (!descarteCorreto) {
            // Atividade 1: notifica a destinacao incorreta
            Notificacao notificacao = Notificacao.builder()
                    .pontoColeta(ponto)
                    .tipo("DESTINACAO_INCORRETA")
                    .titulo("Descarte incorreto identificado")
                    .mensagem("O residuo '" + tipoInformado.getNome() + "' foi descartado incorretamente no ponto '"
                            + ponto.getNome() + "'. " + observacao)
                    .prioridade(Prioridade.ALTA)
                    .build();
            notificacaoRepository.save(notificacao);
        } else {
            // Atividade 2: atualiza quantidade e percentual de ocupacao do ponto
            BigDecimal percentualAnterior = ponto.getPercentualOcupacao();
            BigDecimal novaQuantidade = ponto.getQuantidadeAtualKg().add(request.getQuantidadeKg());
            BigDecimal novoPercentual = novaQuantidade
                    .multiply(BigDecimal.valueOf(100))
                    .divide(ponto.getCapacidadeKg(), 2, RoundingMode.HALF_UP);

            ponto.setQuantidadeAtualKg(novaQuantidade);
            ponto.setPercentualOcupacao(novoPercentual);

            StatusPonto novoStatus;
            if (novoPercentual.compareTo(BigDecimal.valueOf(100)) >= 0) {
                novoStatus = StatusPonto.CHEIO;
            } else if (novoPercentual.compareTo(ponto.getLimiteAlertaPercentual()) >= 0) {
                novoStatus = StatusPonto.ALERTA;
            } else {
                novoStatus = StatusPonto.DISPONIVEL;
            }
            ponto.setStatus(novoStatus);
            ponto = pontoColetaRepository.save(ponto);

            // Atividade 3: gera coleta automatica quando o limite e cruzado
            boolean cruzouLimiteAgora = percentualAnterior.compareTo(ponto.getLimiteAlertaPercentual()) < 0
                    && novoPercentual.compareTo(ponto.getLimiteAlertaPercentual()) >= 0;
            if (cruzouLimiteAgora) {
                Coleta coleta = Coleta.builder()
                        .pontoColeta(ponto)
                        .dataSolicitacao(LocalDate.now())
                        .status(StatusColeta.SOLICITADA)
                        .observacao("Coleta gerada automaticamente ao atingir " + novoPercentual + "% de ocupacao")
                        .build();
                coleta = coletaRepository.save(coleta);

                Notificacao notificacao = Notificacao.builder()
                        .pontoColeta(ponto)
                        .coleta(coleta)
                        .tipo("LIMITE_COLETA")
                        .titulo("Limite de ocupacao atingido")
                        .mensagem("O ponto '" + ponto.getNome() + "' atingiu " + novoPercentual
                                + "% de ocupacao. Uma coleta foi solicitada automaticamente.")
                        .prioridade(Prioridade.ALTA)
                        .build();
                notificacaoRepository.save(notificacao);
            }
        }

        return toResponse(descarte);
    }

    private DescarteResponse toResponse(Descarte d) {
        return DescarteResponse.builder()
                .id(d.getId())
                .idPontoColeta(d.getPontoColeta().getId())
                .nomePontoColeta(d.getPontoColeta().getNome())
                .idTipoResiduo(d.getTipoResiduo().getId())
                .nomeTipoResiduo(d.getTipoResiduo().getNome())
                .dataDescarte(d.getDataDescarte())
                .quantidadeKg(d.getQuantidadeKg())
                .origem(d.getOrigem())
                .descarteCorreto(d.getDescarteCorreto())
                .observacao(d.getObservacao())
                .build();
    }
}