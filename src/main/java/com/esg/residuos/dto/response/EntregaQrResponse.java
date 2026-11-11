package com.esg.residuos.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaQrResponse {
    private String codigo;
    private String nomePontoColeta;
    private String nomeTipoResiduo;
    private BigDecimal quantidadeKg;
    private BigDecimal moedas;
}
