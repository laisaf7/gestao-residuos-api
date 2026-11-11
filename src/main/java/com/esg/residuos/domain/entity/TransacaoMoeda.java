package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACAO_MOEDA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoMoeda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_transacao_moeda_gen")
    @SequenceGenerator(name = "seq_transacao_moeda_gen", sequenceName = "SEQ_TRANSACAO_MOEDA", allocationSize = 1)
    @Column(name = "ID_TRANSACAO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoTransacao tipo;

    @Column(name = "VALOR", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "DESCRICAO", nullable = false, length = 200)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMERCIO")
    private Comercio comercio;

    @Column(name = "CODIGO_VOUCHER", unique = true, length = 12)
    private String codigoVoucher;

    @Builder.Default
    @Column(name = "DATA_TRANSACAO", nullable = false)
    private LocalDateTime dataTransacao = LocalDateTime.now();
}
