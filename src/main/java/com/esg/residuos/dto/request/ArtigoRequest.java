package com.esg.residuos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtigoRequest {

    @NotBlank(message = "O titulo e obrigatorio")
    @Size(max = 120, message = "O titulo deve ter no maximo 120 caracteres")
    private String titulo;

    @NotNull(message = "O material e obrigatorio")
    @Pattern(regexp = "Plástico|Papel|Vidro|Metal|Tecido|Madeira|Outros",
            message = "Material invalido. Use: Plástico, Papel, Vidro, Metal, Tecido, Madeira ou Outros")
    private String material;

    @NotBlank(message = "O passo a passo e obrigatorio")
    @Size(max = 4000, message = "O passo a passo deve ter no maximo 4000 caracteres")
    private String conteudo;
}
