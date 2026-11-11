package com.esg.residuos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sistema de Gestao de Residuos e Reciclagem - API RESTful.
 * <p>
 * Projeto academico ESG (pilar Environmental) responsavel por expor os
 * recursos de rastreamento de coleta seletiva, descarte correto de residuos,
 * geracao automatica de solicitacoes de coleta e notificacoes aos usuarios.
 */
@SpringBootApplication
public class GestaoResiduosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestaoResiduosApplication.class, args);
    }
}
