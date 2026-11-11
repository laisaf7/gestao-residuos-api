package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.converter.SimNaoConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DESCARTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Descarte {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_descarte_gen")
    @SequenceGenerator(name = "seq_descarte_gen", sequenceName = "SEQ_DESCARTE", allocationSize = 1)
    @Column(name = "ID_DESCARTE")
    private Long id;

    @NotNull(message = "O ponto de coleta e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta pontoColeta;

    @NotNull(message = "O tipo de residuo descartado e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TIPO_RESIDUO", nullable = false)
    private TipoResiduo tipoResiduo;

    @Builder.Default
    @Column(name = "DATA_DESCARTE", nullable = false)
    private LocalDate dataDescarte = LocalDate.now();

    @NotNull(message = "A quantidade descartada e obrigatoria")
    @DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero")
    @Column(name = "QUANTIDADE_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeKg;

    @Size(max = 100)
    @Column(name = "ORIGEM", length = 100)
    private String origem;

    @Builder.Default
    @Convert(converter = SimNaoConverter.class)
    @Column(name = "DESCARTE_CORRETO", nullable = false, length = 1)
    private Boolean descarteCorreto = Boolean.TRUE;

    @Size(max = 500)
    @Column(name = "OBSERVACAO", length = 500)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;
}
