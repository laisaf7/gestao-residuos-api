package com.esg.residuos.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal precoReais;
    /** Nulo = so vende em reais. */
    private BigDecimal precoMoedas;
    private Long idPontoColeta;
    private String nomeAtelie;
    private String bairro;
    private String endereco;
    /** Material que o atelie recebe (nome do tipo de residuo). */
    private String material;
    /** Username do artesao. */
    private String artesao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    /** Quando true, a foto esta em GET /api/produtos/{id}/imagem. */
    private boolean temImagem;
}
