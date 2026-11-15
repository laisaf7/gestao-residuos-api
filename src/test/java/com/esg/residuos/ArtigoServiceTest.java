package com.esg.residuos;

import com.esg.residuos.domain.entity.Artigo;
import com.esg.residuos.dto.request.ArtigoRequest;
import com.esg.residuos.dto.response.ArtigoResponse;
import com.esg.residuos.repository.ArtigoRepository;
import com.esg.residuos.service.ArtigoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtigoServiceTest {

    @Mock private ArtigoRepository repository;
    @InjectMocks private ArtigoService service;

    @Test
    void criarUsaUsuarioLogadoComoAutor() {
        when(repository.save(any(Artigo.class))).thenAnswer(inv -> inv.getArgument(0));

        ArtigoResponse r = service.criar("maria",
                new ArtigoRequest("  Tapete de camiseta  ", "Tecido", " Corte em tiras e trance. "));

        assertThat(r.getAutor()).isEqualTo("maria");
        assertThat(r.getTitulo()).isEqualTo("Tapete de camiseta");
        assertThat(r.getConteudo()).isEqualTo("Corte em tiras e trance.");
        assertThat(r.getDataCriacao()).isNotNull();
    }
}
