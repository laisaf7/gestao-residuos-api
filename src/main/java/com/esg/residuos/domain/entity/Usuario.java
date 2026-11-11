package com.esg.residuos.domain.entity;

import com.esg.residuos.domain.converter.SimNaoConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario_gen")
    @SequenceGenerator(name = "seq_usuario_gen", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @Column(name = "ID_USUARIO")
    private Long id;

    @NotBlank
    @Column(name = "USERNAME", nullable = false, unique = true, length = 60)
    private String username;

    @NotBlank
    @Column(name = "PASSWORD", nullable = false, length = 200)
    private String password;

    @NotBlank
    @Column(name = "ROLE", nullable = false, length = 20)
    private String role;

    @Builder.Default
    @Convert(converter = SimNaoConverter.class)
    @Column(name = "ATIVO", nullable = false, length = 1)
    private Boolean ativo = Boolean.TRUE;

    @Builder.Default
    @Column(name = "SALDO_ECOMOEDAS", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoEcoMoedas = BigDecimal.ZERO;
}
