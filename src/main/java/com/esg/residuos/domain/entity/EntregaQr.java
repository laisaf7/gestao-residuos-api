package com.esg.residuos.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** QR Code de uso unico gerado pelo ecoponto no momento da pesagem. */
@Entity
@Table(name = "ENTREGA_QR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaQr {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_entrega_qr_gen")
    @SequenceGenerator(name = "seq_entrega_qr_gen", sequenceName = "SEQ_ENTREGA_QR", allocationSize = 1)
    @Column(name = "ID_ENTREGA_QR")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 36)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta pontoColeta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TIPO_RESIDUO", nullable = false)
    private TipoResiduo tipoResiduo;

    @Column(name = "QUANTIDADE_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeKg;

    @Builder.Default
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_RESGATE")
    private Usuario usuarioResgate;

    @Column(name = "DATA_RESGATE")
    private LocalDateTime dataResgate;
}
