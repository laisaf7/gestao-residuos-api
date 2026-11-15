package com.esg.residuos.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtigoResponse {
    private Long id;
    private String titulo;
    private String material;
    private String conteudo;
    private String autor;
    private LocalDateTime dataCriacao;
}
