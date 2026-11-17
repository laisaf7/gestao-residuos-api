package com.esg.residuos;

import com.esg.residuos.domain.entity.*;
import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.request.EntregaQrRequest;
import com.esg.residuos.dto.request.PontoColetaRequest;
import com.esg.residuos.dto.response.PontoColetaResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.repository.*;
import com.esg.residuos.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArtesaoServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PontoColetaRepository pontoRepository;
    @Mock private TipoResiduoService tipoResiduoService;

    private ArtesaoService service;
    private Usuario ana;
    private final TipoResiduo plastico = TipoResiduo.builder().id(2L).nome("PLASTICO").moedasPorKg(new BigDecimal("5")).build();

    @BeforeEach
    void setUp() {
        service = new ArtesaoService(usuarioRepository, pontoRepository, new PontoColetaService(pontoRepository, tipoResiduoService));
        ana = Usuario.builder().id(1L).username("ana").role("USER").saldoEcoMoedas(BigDecimal.ZERO).build();
        when(usuarioRepository.findForUpdateByUsernameIgnoreCase("ana")).thenReturn(Optional.of(ana));
        when(tipoResiduoService.buscarEntidade(2L)).thenReturn(plastico);
        when(pontoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private PontoColetaRequest pedido(String kg) {
        return PontoColetaRequest.builder().idTipoResiduo(2L).nome("Atelie da Ana").capacidadeKg(new BigDecimal(kg)).build();
    }

    @Test
    void cadastroCriaAtelieETrocaPapel() {
        PontoColetaResponse r = service.cadastrar("ana", pedido("80"));

        assertThat(ana.getRole()).isEqualTo("ARTESAO");
        assertThat(r.getArtesao()).isTrue();
        assertThat(r.getCapacidadeKg()).isEqualByComparingTo("80");
        assertThat(r.getStatus()).isEqualTo(StatusPonto.DISPONIVEL);
    }

    @Test
    void recusaCapacidadeAcimaDe100Kg() {
        assertThatThrownBy(() -> service.cadastrar("ana", pedido("150"))).isInstanceOf(BusinessException.class);
        assertThat(ana.getRole()).isEqualTo("USER");
        verify(pontoRepository, never()).save(any());
    }

    @Test
    void artesaoNaoCadastraSegundoAtelie() {
        ana.setRole("ARTESAO");
        assertThatThrownBy(() -> service.cadastrar("ana", pedido("50"))).isInstanceOf(BusinessException.class);
    }

    @Test
    void esvaziarZeraOcupacao() {
        PontoColeta atelie = PontoColeta.builder().id(5L).nome("Atelie").tipoResiduo(plastico).artesao(ana)
                .capacidadeKg(new BigDecimal("50")).quantidadeAtualKg(new BigDecimal("50"))
                .percentualOcupacao(new BigDecimal("100")).status(StatusPonto.CHEIO).build();
        when(pontoRepository.findByArtesaoUsernameIgnoreCase("ana")).thenReturn(Optional.of(atelie));

        service.esvaziar("ana");

        assertThat(atelie.getQuantidadeAtualKg()).isEqualByComparingTo("0");
        assertThat(atelie.getStatus()).isEqualTo(StatusPonto.DISPONIVEL);
    }

    @Test
    void artesaoSoGeraQrNoProprioAtelie() {
        PontoColetaService pontos = mock(PontoColetaService.class);
        TipoResiduoService tipos = mock(TipoResiduoService.class);
        EntregaQrRepository entregas = mock(EntregaQrRepository.class);
        CarteiraService carteira = new CarteiraService(usuarioRepository, entregas, mock(TransacaoMoedaRepository.class),
                mock(ComercioRepository.class), mock(DescarteRepository.class), mock(DescarteService.class), pontos, tipos);
        Usuario bia = Usuario.builder().id(2L).username("bia").build();
        when(pontos.buscarEntidade(5L)).thenReturn(PontoColeta.builder().id(5L).nome("Atelie da Bia").tipoResiduo(plastico).artesao(bia).build());
        when(pontos.buscarEntidade(1L)).thenReturn(PontoColeta.builder().id(1L).nome("Ecoponto").tipoResiduo(plastico).build());
        when(tipos.buscarEntidade(2L)).thenReturn(plastico);
        when(entregas.save(any())).thenAnswer(i -> i.getArgument(0));
        EntregaQrRequest noAtelie = EntregaQrRequest.builder().idPontoColeta(5L).idTipoResiduo(2L).quantidadeKg(BigDecimal.ONE).build();
        EntregaQrRequest noEcoponto = EntregaQrRequest.builder().idPontoColeta(1L).idTipoResiduo(2L).quantidadeKg(BigDecimal.ONE).build();

        assertThat(carteira.gerarQr(noAtelie, "bia").getMoedas()).isEqualByComparingTo("5");
        assertThatThrownBy(() -> carteira.gerarQr(noAtelie, "ana")).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> carteira.gerarQr(noEcoponto, "bia")).isInstanceOf(AccessDeniedException.class);
        assertThat(carteira.gerarQr(noEcoponto, null).getCodigo()).isNotBlank();
    }
}
