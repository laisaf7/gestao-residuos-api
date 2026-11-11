package com.esg.residuos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResgateRequest {

    @NotBlank(message = "O codigo do QR Code e obrigatorio")
    @Size(max = 36)
    private String codigo;
}
