package com.esg.residuos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescarteRequest {

    @NotNull(message = "O id do ponto de coleta e obrigatorio")
    private Long idPontoColeta;

    @NotNull(message = "O id do tipo de residuo descartado e obrigatorio")
    private Long idTipoResiduo;

    @NotNull(message = "A quantidade descartada e obrigatoria")
    @DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidadeKg;

    @Size(max = 100)
    private String origem;
}
