package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.converter.SimNaoConverter;
import com.esg.residuos.domain.enums.StatusPonto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PONTO_COLETA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ponto_coleta_gen")
    @SequenceGenerator(name = "seq_ponto_coleta_gen", sequenceName = "SEQ_PONTO_COLETA", allocationSize = 1)
    @Column(name = "ID_PONTO_COLETA")
    private Long id;

    @NotNull(message = "O tipo de residuo aceito e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TIPO_RESIDUO", nullable = false)
    private TipoResiduo tipoResiduo;

    @NotBlank(message = "O nome do ponto de coleta e obrigatorio")
    @Size(max = 120)
    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @Size(max = 300)
    @Column(name = "DESCRICAO", length = 300)
    private String descricao;

    @Size(max = 200)
    @Column(name = "ENDERECO", length = 200)
    private String endereco;

    @Size(max = 100)
    @Column(name = "BAIRRO", length = 100)
    private String bairro;

    @Size(max = 100)
    @Column(name = "CIDADE", length = 100)
    private String cidade;

    @Size(max = 2)
    @Column(name = "ESTADO", length = 2)
    private String estado;

    @Size(max = 10)
    @Column(name = "CEP", length = 10)
    private String cep;

    @Column(name = "LATITUDE", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 9, scale = 6)
    private BigDecimal longitude;

    @NotNull(message = "A capacidade em Kg e obrigatoria")
    @DecimalMin(value = "0.01", message = "A capacidade deve ser maior que zero")
    @Column(name = "CAPACIDADE_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacidadeKg;

    @Builder.Default
    @Column(name = "QUANTIDADE_ATUAL_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeAtualKg = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "PERCENTUAL_OCUPACAO", nullable = false, precision = 6, scale = 2)
    private BigDecimal percentualOcupacao = BigDecimal.ZERO;

    @Builder.Default
    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "100.0")
    @Column(name = "LIMITE_ALERTA_PERCENTUAL", nullable = false, precision = 5, scale = 2)
    private BigDecimal limiteAlertaPercentual = new BigDecimal("80");

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusPonto status = StatusPonto.DISPONIVEL;

    @Builder.Default
    @Column(name = "DATA_INSTALACAO", nullable = false)
    private LocalDate dataInstalacao = LocalDate.now();

    @Column(name = "DATA_ULTIMA_COLETA")
    private LocalDate dataUltimaColeta;

    @Builder.Default
    @Convert(converter = SimNaoConverter.class)
    @Column(name = "ATIVO", nullable = false, length = 1)
    private Boolean ativo = Boolean.TRUE;
}
