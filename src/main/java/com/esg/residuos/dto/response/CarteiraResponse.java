package com.esg.residuos.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteiraResponse {
    private BigDecimal saldo;
    private BigDecimal kgDesviados;
    private List<TransacaoResponse> transacoes;
    /** Ids dos atelies onde o usuario tem cupom de desconto (entregou material e ainda nao usou). */
    private List<Long> ateliesComDesconto;
}
