package com.esg.residuos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "O usuario e obrigatorio")
    private String username;

    @NotBlank(message = "A senha e obrigatoria")
    private String password;
}
