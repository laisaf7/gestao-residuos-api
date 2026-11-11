package com.esg.residuos.dto.response;

import com.esg.residuos.domain.enums.StatusPonto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoColetaResponse {
    private Long id;
    private Long idTipoResiduo;
    private String nomeTipoResiduo;
    private String nome;
    private String descricao;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal capacidadeKg;
    private BigDecimal quantidadeAtualKg;
    private BigDecimal percentualOcupacao;
    private BigDecimal limiteAlertaPercentual;
    private StatusPonto status;
    private LocalDate dataInstalacao;
    private LocalDate dataUltimaColeta;
    private Boolean ativo;
}
