package com.esg.residuos.dto.response;

import com.esg.residuos.domain.enums.StatusColeta;
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
public class ColetaResponse {
    private Long id;
    private Long idPontoColeta;
    private String nomePontoColeta;
    private LocalDate dataSolicitacao;
    private LocalDate dataAgendada;
    private LocalDate dataColeta;
    private BigDecimal quantidadeColetadaKg;
    private StatusColeta status;
    private String destinoFinal;
    private String observacao;
}
