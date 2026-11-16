package com.esg.residuos;

import com.esg.residuos.domain.entity.Artigo;
import com.esg.residuos.dto.request.ArtigoRequest;
import com.esg.residuos.dto.response.ArtigoResponse;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.ArtigoRepository;
import com.esg.residuos.service.ArtigoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArtigoServiceTest {

    @Mock private ArtigoRepository repository;
    @InjectMocks private ArtigoService service;

    private ArtigoResponse criar(String imagemBase64) {
        when(repository.save(any(Artigo.class))).thenAnswer(inv -> inv.getArgument(0));
        return service.criar("maria",
                ArtigoRequest.builder().titulo("  Tapete de camiseta  ").material("Tecido")
                        .conteudo(" Corte em tiras e trance. ").imagemBase64(imagemBase64).build());
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

    // ---- edicao e exclusao ----

    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 1, 2};

    private Artigo artigoDaMaria() {
        Artigo artigo = Artigo.builder().id(7L).titulo("Antigo").material("Papel").conteudo("x")
                .autor("maria").imagem(JPEG).build();
        when(repository.findById(7L)).thenReturn(Optional.of(artigo));
        return artigo;
    }

    private ArtigoRequest edicao(String imagemBase64, boolean removerImagem) {
        return ArtigoRequest.builder().titulo(" Novo ").material("Tecido").conteudo(" passo ")
                .imagemBase64(imagemBase64).removerImagem(removerImagem).build();
    }

    @Test
    void donoEditaEMantemFotoQuandoNaoEnviaOutra() {
        Artigo artigo = artigoDaMaria();

        ArtigoResponse r = service.editar(7L, "maria", edicao(null, false));

        assertThat(r.getTitulo()).isEqualTo("Novo");
        assertThat(r.getMaterial()).isEqualTo("Tecido");
        assertThat(r.getDataAtualizacao()).isNotNull();
        assertThat(artigo.getImagem()).isSameAs(JPEG);
    }

    @Test
    void donoRemoveFoto() {
        Artigo artigo = artigoDaMaria();
        service.editar(7L, "maria", edicao(null, true));
        assertThat(artigo.getImagem()).isNull();
    }

    @Test
    void novaFotoTemPrioridadeSobreRemover() {
        Artigo artigo = artigoDaMaria();
        byte[] outra = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 9};
        service.editar(7L, "maria", edicao(Base64.getEncoder().encodeToString(outra), true));
        assertThat(artigo.getImagem()).isEqualTo(outra);
    }

    @Test
    void outroUsuarioNaoEditaNemExclui() {
        Artigo artigo = artigoDaMaria();

        assertThatThrownBy(() -> service.editar(7L, "joao", edicao(null, false)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> service.excluir(7L, "joao", false))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(artigo.getTitulo()).isEqualTo("Antigo");
        verify(repository, never()).delete(any());
    }

    @Test
    void donoExclui() {
        Artigo artigo = artigoDaMaria();
        service.excluir(7L, "maria", false);
        verify(repository).delete(artigo);
    }

    @Test
    void adminExcluiArtigoDeOutroUsuario() {
        Artigo artigo = artigoDaMaria();
        service.excluir(7L, "admin", true);
        verify(repository).delete(artigo);
    }

    @Test
    void adminNaoEditaArtigoDeOutroUsuario() {
        artigoDaMaria();
        assertThatThrownBy(() -> service.editar(7L, "admin", edicao(null, false)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void artigoInexistenteDa404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.excluir(99L, "maria", false)).isInstanceOf(ResourceNotFoundException.class);
    }
}
