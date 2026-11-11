package com.esg.residuos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoRequest {

    @NotNull(message = "O id do comercio e obrigatorio")
    private Long idComercio;

    @NotNull(message = "O valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal valor;

    @NotNull(message = "O tipo e obrigatorio")
    @Pattern(regexp = "VOUCHER|TRANSFERENCIA", message = "O tipo deve ser VOUCHER ou TRANSFERENCIA")
    private String tipo;
}
