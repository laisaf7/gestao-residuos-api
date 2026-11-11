package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.converter.SimNaoConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "TIPO_RESIDUO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tipo_residuo_gen")
    @SequenceGenerator(name = "seq_tipo_residuo_gen", sequenceName = "SEQ_TIPO_RESIDUO", allocationSize = 1)
    @Column(name = "ID_TIPO_RESIDUO")
    private Long id;

    @NotBlank(message = "O nome do tipo de residuo e obrigatorio")
    @Size(max = 100)
    @Column(name = "NOME", nullable = false, unique = true, length = 100)
    private String nome;

    @NotBlank(message = "A categoria e obrigatoria")
    @Size(max = 50)
    @Column(name = "CATEGORIA", nullable = false, length = 50)
    private String categoria;

    @Size(max = 30)
    @Column(name = "COR_IDENTIFICACAO", length = 30)
    private String corIdentificacao;

    @NotBlank(message = "A destinacao correta e obrigatoria")
    @Size(max = 300)
    @Column(name = "DESTINACAO_CORRETA", nullable = false, length = 300)
    private String destinacaoCorreta;

    @Size(max = 500)
    @Column(name = "IMPACTO_AMBIENTAL", length = 500)
    private String impactoAmbiental;

    @Builder.Default
    @Convert(converter = SimNaoConverter.class)
    @Column(name = "ATIVO", nullable = false, length = 1)
    private Boolean ativo = Boolean.TRUE;

    @Builder.Default
    @Column(name = "MOEDAS_POR_KG", nullable = false, precision = 8, scale = 2)
    private BigDecimal moedasPorKg = BigDecimal.ONE;
}
