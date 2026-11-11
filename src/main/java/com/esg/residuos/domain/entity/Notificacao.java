package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.converter.SimNaoConverter;
import com.esg.residuos.domain.enums.Prioridade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICACAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_notificacao_gen")
    @SequenceGenerator(name = "seq_notificacao_gen", sequenceName = "SEQ_NOTIFICACAO", allocationSize = 1)
    @Column(name = "ID_NOTIFICACAO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta pontoColeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COLETA")
    private Coleta coleta;

    @NotBlank
    @Size(max = 40)
    @Column(name = "TIPO", nullable = false, length = 40)
    private String tipo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Size(max = 500)
    @Column(name = "MENSAGEM", nullable = false, length = 500)
    private String mensagem;

    @Builder.Default
    @Column(name = "DATA_NOTIFICACAO", nullable = false)
    private LocalDateTime dataNotificacao = LocalDateTime.now();

    @Builder.Default
    @Convert(converter = SimNaoConverter.class)
    @Column(name = "LIDA", nullable = false, length = 1)
    private Boolean lida = Boolean.FALSE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "PRIORIDADE", nullable = false, length = 10)
    private Prioridade prioridade = Prioridade.MEDIA;
}
