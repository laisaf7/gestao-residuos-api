package com.esg.residuos.dto.response;

import com.esg.residuos.domain.enums.TipoTransacao;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoResponse {
    private Long id;
    private TipoTransacao tipo;
    private BigDecimal valor;
    private String descricao;
    private String codigoVoucher;
    private LocalDateTime dataTransacao;
}
