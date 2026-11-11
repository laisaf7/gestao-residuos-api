package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.enums.StatusColeta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "COLETA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_coleta_gen")
    @SequenceGenerator(name = "seq_coleta_gen", sequenceName = "SEQ_COLETA", allocationSize = 1)
    @Column(name = "ID_COLETA")
    private Long id;

    @NotNull(message = "O ponto de coleta e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta pontoColeta;

    @Builder.Default
    @Column(name = "DATA_SOLICITACAO", nullable = false)
    private LocalDate dataSolicitacao = LocalDate.now();

    @Column(name = "DATA_AGENDADA")
    private LocalDate dataAgendada;

    @Column(name = "DATA_COLETA")
    private LocalDate dataColeta;

    @Column(name = "QUANTIDADE_COLETADA_KG", precision = 10, scale = 2)
    private BigDecimal quantidadeColetadaKg;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusColeta status = StatusColeta.SOLICITADA;

    @Size(max = 300)
    @Column(name = "DESTINO_FINAL", length = 300)
    private String destinoFinal;

    @Size(max = 500)
    @Column(name = "OBSERVACAO", length = 500)
    private String observacao;
}
