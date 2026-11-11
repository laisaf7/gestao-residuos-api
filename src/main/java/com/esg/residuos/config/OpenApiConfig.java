package com.esg.residuos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Configuracao da documentacao OpenAPI/Swagger, incluindo o esquema de autenticacao Bearer (JWT). */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Gestao de Residuos e Reciclagem",
                version = "1.0.0",
                description = "Servico RESTful para rastreamento de coleta seletiva, descarte correto de "
                        + "residuos, alertas automaticos de limite de ocupacao e notificacoes de destinacao "
                        + "correta. Projeto academico ESG (pilar Environmental)."
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
