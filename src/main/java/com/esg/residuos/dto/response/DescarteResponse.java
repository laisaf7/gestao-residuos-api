package com.esg.residuos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescarteResponse {
    private Long id;
    private Long idPontoColeta;
    private String nomePontoColeta;
    private Long idTipoResiduo;
    private String nomeTipoResiduo;
    private LocalDate dataDescarte;
    private BigDecimal quantidadeKg;
    private String origem;
    private Boolean descarteCorreto;
    private String observacao;
}
