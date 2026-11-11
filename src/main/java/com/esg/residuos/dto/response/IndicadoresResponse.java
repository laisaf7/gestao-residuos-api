package com.esg.residuos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicadoresResponse {
    private long totalPontosColeta;
    private long pontosDisponiveis;
    private long pontosEmAlerta;
    private long pontosCheios;
    private long totalDescartes;
    private long descartesCorretos;
    private long descartesIncorretos;
    private BigDecimal taxaDescarteCorretoPercentual;
    private BigDecimal totalRecicladoKgAcumulado;
    private long coletasSolicitadas;
    private long coletasConcluidas;
    private long notificacoesNaoLidas;
}
