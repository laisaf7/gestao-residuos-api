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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArtigoServiceTest {

    @Mock private ArtigoRepository repository;
    @InjectMocks private ArtigoService service;

    private ArtigoResponse criar(String imagemBase64) {
        when(repository.save(any(Artigo.class))).thenAnswer(inv -> inv.getArgument(0));
        return service.criar("maria",
                new ArtigoRequest("  Tapete de camiseta  ", "Tecido", " Corte em tiras e trance. ", imagemBase64));
    }

    @Test
    void criarUsaUsuarioLogadoComoAutor() {
        ArtigoResponse r = criar(null);

        assertThat(r.getAutor()).isEqualTo("maria");
        assertThat(r.getTitulo()).isEqualTo("Tapete de camiseta");
        assertThat(r.getConteudo()).isEqualTo("Corte em tiras e trance.");
        assertThat(r.getDataCriacao()).isNotNull();
        assertThat(r.isTemImagem()).isFalse();
    }

    @Test
    void criarComJpegGuardaFoto() {
        byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 1, 2};
        assertThat(criar(Base64.getEncoder().encodeToString(jpeg)).isTemImagem()).isTrue();
    }

    @Test
    void recusaFotoQueNaoEJpeg() {
        String png = Base64.getEncoder().encodeToString(new byte[]{(byte) 0x89, 'P', 'N', 'G'});
        assertThatThrownBy(() -> criar(png)).hasMessage("A foto deve estar no formato JPEG");
    }

    @Test
    void recusaBase64Corrompido() {
        assertThatThrownBy(() -> criar("@@nao-e-base64@@")).hasMessage("A foto enviada esta corrompida");
    }
}
