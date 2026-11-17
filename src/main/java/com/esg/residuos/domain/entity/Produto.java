package com.esg.residuos.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Produto feito por um artesao com material reciclado. */
@Entity
@Table(name = "PRODUTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_produto_gen")
    @SequenceGenerator(name = "seq_produto_gen", sequenceName = "SEQ_PRODUTO", allocationSize = 1)
    @Column(name = "ID_PRODUTO")
    private Long id;

    /** Atelie (ponto de coleta) do artesao que vende. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta atelie;

    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @Column(name = "DESCRICAO", length = 1000)
    private String descricao;

    @Column(name = "PRECO_REAIS", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoReais;

    /** Nulo = vendido so em reais, direto com o artesao. */
    @Column(name = "PRECO_MOEDAS", precision = 10, scale = 2)
    private BigDecimal precoMoedas;

    @Builder.Default
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    /** Foto JPEG. A listagem usa projecao (ProdutoRepository) para nao carregar estes bytes. */
    @Column(name = "IMAGEM")
    private byte[] imagem;
}
