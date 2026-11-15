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

    /** Foto JPEG opcional em Base64 (o app reduz para ate ~1280 px antes de enviar). */
    @Size(max = 1_400_000, message = "A foto deve ter no maximo 1 MB")
    private String imagemBase64;

    /** Na edicao: true apaga a foto atual (ignorado se imagemBase64 vier preenchido). */
    private boolean removerImagem;
}
