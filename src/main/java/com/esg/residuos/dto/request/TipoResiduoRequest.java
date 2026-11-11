package com.esg.residuos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class TipoResiduoRequest {

    @NotBlank(message = "O nome e obrigatorio")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "A categoria e obrigatoria")
    @Size(max = 50)
    private String categoria;

    @Size(max = 30)
    private String corIdentificacao;

    @NotBlank(message = "A destinacao correta e obrigatoria")
    @Size(max = 300)
    private String destinacaoCorreta;

    @Size(max = 500)
    private String impactoAmbiental;

    private Boolean ativo;
}
