package com.esg.residuos.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColetaRequest {

    @NotNull(message = "O id do ponto de coleta e obrigatorio")
    private Long idPontoColeta;

    private LocalDate dataAgendada;

    @Size(max = 300)
    private String destinoFinal;

    @Size(max = 500)
    private String observacao;
}
