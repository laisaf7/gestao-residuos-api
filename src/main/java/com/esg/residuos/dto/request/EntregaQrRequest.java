package com.esg.residuos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaQrRequest {

    @NotNull(message = "O id do ponto de coleta e obrigatorio")
    private Long idPontoColeta;

    @NotNull(message = "O id do tipo de residuo e obrigatorio")
    private Long idTipoResiduo;

    @NotNull(message = "A quantidade e obrigatoria")
    @DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal quantidadeKg;
}
