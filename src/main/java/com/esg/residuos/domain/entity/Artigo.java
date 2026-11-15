package com.esg.residuos.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ARTIGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artigo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_artigo_gen")
    @SequenceGenerator(name = "seq_artigo_gen", sequenceName = "SEQ_ARTIGO", allocationSize = 1)
    @Column(name = "ID_ARTIGO")
    private Long id;

    @Column(name = "TITULO", nullable = false, length = 120)
    private String titulo;

    @Column(name = "MATERIAL", nullable = false, length = 30)
    private String material;

    @Column(name = "CONTEUDO", nullable = false, length = 4000)
    private String conteudo;

    @Column(name = "AUTOR", nullable = false, length = 50)
    private String autor;

    @Builder.Default
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    /** Foto JPEG. A listagem usa projecao (ArtigoRepository) para nao carregar estes bytes. */
    @Column(name = "IMAGEM")
    private byte[] imagem;
}
