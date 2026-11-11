package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.converter.SimNaoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "COMERCIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_comercio_gen")
    @SequenceGenerator(name = "seq_comercio_gen", sequenceName = "SEQ_COMERCIO", allocationSize = 1)
    @Column(name = "ID_COMERCIO")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @Column(name = "CATEGORIA", nullable = false, length = 50)
    private String categoria;

    @Column(name = "DESCRICAO", length = 300)
    private String descricao;

    @Column(name = "ENDERECO", length = 200)
    private String endereco;

    @Column(name = "BAIRRO", length = 100)
    private String bairro;

    @Builder.Default
    @Column(name = "SALDO_RECEBIDO", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoRecebido = BigDecimal.ZERO;

    @Builder.Default
    @Convert(converter = SimNaoConverter.class)
    @Column(name = "ATIVO", nullable = false, length = 1)
    private Boolean ativo = Boolean.TRUE;
}
