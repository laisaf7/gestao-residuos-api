package com.esg.residuos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoColetaRequest {

    @NotNull(message = "O id do tipo de residuo e obrigatorio")
    private Long idTipoResiduo;

    @NotBlank(message = "O nome e obrigatorio")
    @Size(max = 120)
    private String nome;

    @Size(max = 300)
    private String descricao;

    @Size(max = 200)
    private String endereco;

    @Size(max = 100)
    private String bairro;

    @Size(max = 100)
    private String cidade;

    @Size(max = 2)
    private String estado;

    @Size(max = 10)
    private String cep;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;

    @NotNull(message = "A capacidade em Kg e obrigatoria")
    @DecimalMin(value = "0.01", message = "A capacidade deve ser maior que zero")
    private BigDecimal capacidadeKg;

    @DecimalMin(value = "0.01")
    @DecimalMax(value = "100.0")
    private BigDecimal limiteAlertaPercentual;
}
