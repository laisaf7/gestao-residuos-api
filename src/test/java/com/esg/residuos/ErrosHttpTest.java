package com.esg.residuos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Erros do Spring MVC devem manter o status correto em vez de virar 500. */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ErrosHttpTest {

    @Autowired private MockMvc mvc;

    @Test
    void rotaInexistenteDa404() throws Exception {
        mvc.perform(get("/api/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Rota nao encontrada"));
    }

    @Test
    void metodoNaoSuportadoDa405() throws Exception {
        mvc.perform(patch("/api/artigos")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void jsonMalFormatadoDa400() throws Exception {
        mvc.perform(post("/api/artigos").contentType(MediaType.APPLICATION_JSON).content("{quebrado"))
                .andExpect(status().isBadRequest());
    }
}
