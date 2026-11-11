package com.esg.residuos.dto.request;

import com.esg.residuos.domain.enums.StatusColeta;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColetaUpdateRequest {

    private LocalDate dataAgendada;

    private StatusColeta status;

    @Size(max = 300)
    private String destinoFinal;

    @Size(max = 500)
    private String observacao;
}
