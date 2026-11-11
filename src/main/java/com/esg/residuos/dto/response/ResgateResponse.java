package com.esg.residuos.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResgateResponse {
    private String nomePontoColeta;
    private String nomeTipoResiduo;
    private BigDecimal quantidadeKg;
    private boolean descarteCorreto;
    private BigDecimal moedasCreditadas;
    private BigDecimal saldo;
}
