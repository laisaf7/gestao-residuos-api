package com.esg.residuos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoRequest {

    @NotBlank(message = "O nome e obrigatorio")
    @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
    private String nome;

    @Size(max = 1000, message = "A descricao deve ter no maximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "O preco em reais e obrigatorio")
    @DecimalMin(value = "0.01", message = "O preco em reais deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preco em reais invalido")
    private BigDecimal precoReais;

    /** Opcional: preenchido, o produto pode ser comprado no app com EcoMoedas. */
    @DecimalMin(value = "0.01", message = "O preco em EcoMoedas deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preco em EcoMoedas invalido")
    private BigDecimal precoMoedas;

    /** Foto JPEG opcional em Base64 (o app reduz para ate ~1280 px antes de enviar). */
    @Size(max = 1_400_000, message = "A foto deve ter no maximo 1 MB")
    private String imagemBase64;

    /** Na edicao: true apaga a foto atual (ignorado se imagemBase64 vier preenchido). */
    private boolean removerImagem;
}
