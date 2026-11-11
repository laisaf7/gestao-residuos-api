package com.esg.residuos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoResiduoResponse {
    private Long id;
    private String nome;
    private String categoria;
    private String corIdentificacao;
    private String destinacaoCorreta;
    private String impactoAmbiental;
    private Boolean ativo;
}
