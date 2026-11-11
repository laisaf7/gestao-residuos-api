package com.esg.residuos.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComercioResponse {
    private Long id;
    private String nome;
    private String categoria;
    private String descricao;
    private String endereco;
    private String bairro;
}
