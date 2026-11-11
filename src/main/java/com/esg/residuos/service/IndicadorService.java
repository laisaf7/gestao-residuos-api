package com.esg.residuos.service;

import com.esg.residuos.domain.enums.StatusColeta;
import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.response.IndicadoresResponse;
import com.esg.residuos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Agrega indicadores gerais de sustentabilidade e desempenho da gestao de residuos. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndicadorService {

    private final PontoColetaRepository pontoColetaRepository;
    private final DescarteRepository descarteRepository;
    private final ColetaRepository coletaRepository;
    private final NotificacaoRepository notificacaoRepository;

    public IndicadoresResponse gerar() {
        long totalPontos = pontoColetaRepository.count();
        long disponiveis = pontoColetaRepository.findByStatus(StatusPonto.DISPONIVEL).size();
        long emAlerta = pontoColetaRepository.findByStatus(StatusPonto.ALERTA).size();
        long cheios = pontoColetaRepository.findByStatus(StatusPonto.CHEIO).size();

        long totalDescartes = descarteRepository.count();
        long corretos = descarteRepository.countByDescarteCorreto(Boolean.TRUE);
        long incorretos = descarteRepository.countByDescarteCorreto(Boolean.FALSE);

        BigDecimal taxaCorreto = totalDescartes == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(corretos)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalDescartes), 2, RoundingMode.HALF_UP);

        BigDecimal totalReciclado = descarteRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getDescarteCorreto()))
                .map(d -> d.getQuantidadeKg())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long solicitadas = coletaRepository.findByStatus(StatusColeta.SOLICITADA).size();
        long concluidas = coletaRepository.findByStatus(StatusColeta.CONCLUIDA).size();

        long naoLidas = notificacaoRepository.findByLida(Boolean.FALSE).size();

        return IndicadoresResponse.builder()
                .totalPontosColeta(totalPontos)
                .pontosDisponiveis(disponiveis)
                .pontosEmAlerta(emAlerta)
                .pontosCheios(cheios)
                .totalDescartes(totalDescartes)
                .descartesCorretos(corretos)
                .descartesIncorretos(incorretos)
                .taxaDescarteCorretoPercentual(taxaCorreto)
                .totalRecicladoKgAcumulado(totalReciclado)
                .coletasSolicitadas(solicitadas)
                .coletasConcluidas(concluidas)
                .notificacoesNaoLidas(naoLidas)
                .build();
    }
}
