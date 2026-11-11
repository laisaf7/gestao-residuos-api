package com.esg.residuos;

import com.esg.residuos.domain.entity.*;
import com.esg.residuos.dto.request.DescarteRequest;
import com.esg.residuos.dto.request.PagamentoRequest;
import com.esg.residuos.dto.response.DescarteResponse;
import com.esg.residuos.dto.response.ResgateResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.repository.*;
import com.esg.residuos.service.CarteiraService;
import com.esg.residuos.service.DescarteService;
import com.esg.residuos.service.PontoColetaService;
import com.esg.residuos.service.TipoResiduoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CarteiraServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EntregaQrRepository entregaQrRepository;
    @Mock private TransacaoMoedaRepository transacaoRepository;
    @Mock private ComercioRepository comercioRepository;
    @Mock private DescarteRepository descarteRepository;
    @Mock private DescarteService descarteService;
    @Mock private PontoColetaService pontoColetaService;
    @Mock private TipoResiduoService tipoResiduoService;

    @InjectMocks
    private CarteiraService service;

    private Usuario usuario;
    private EntregaQr entrega;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(1L).username("ana").saldoEcoMoedas(new BigDecimal("10.00")).build();
        TipoResiduo plastico = TipoResiduo.builder().id(2L).nome("PLASTICO").moedasPorKg(new BigDecimal("5")).build();
        PontoColeta ponto = PontoColeta.builder().id(3L).nome("Ponto").tipoResiduo(plastico).build();
        entrega = EntregaQr.builder().codigo("abc").pontoColeta(ponto).tipoResiduo(plastico)
                .quantidadeKg(new BigDecimal("2.50")).build();
        when(usuarioRepository.findForUpdateByUsernameIgnoreCase("ana")).thenReturn(Optional.of(usuario));
        when(entregaQrRepository.findForUpdateByCodigo("abc")).thenReturn(Optional.of(entrega));
    }

    private void descarteRetorna(boolean correto) {
        when(descarteService.registrar(any(DescarteRequest.class), eq(usuario)))
                .thenReturn(DescarteResponse.builder().descarteCorreto(correto).nomePontoColeta("Ponto").build());
    }

    @Test
    void resgateConverteKgEmMoedasEMarcaQrComoUsado() {
        descarteRetorna(true);

        ResgateResponse r = service.resgatar("ana", "abc");

        assertThat(r.getMoedasCreditadas()).isEqualByComparingTo("12.50");
        assertThat(r.getSaldo()).isEqualByComparingTo("22.50");
        assertThat(entrega.getUsuarioResgate()).isSameAs(usuario);
    }

    @Test
    void qrNaoPodeSerResgatadoDuasVezes() {
        entrega.setUsuarioResgate(usuario);

        assertThatThrownBy(() -> service.resgatar("ana", "abc")).isInstanceOf(BusinessException.class);

        assertThat(usuario.getSaldoEcoMoedas()).isEqualByComparingTo("10.00");
        verifyNoInteractions(descarteService);
    }

    @Test
    void descarteIncorretoNaoGeraMoedas() {
        descarteRetorna(false);

        ResgateResponse r = service.resgatar("ana", "abc");

        assertThat(r.getMoedasCreditadas()).isEqualByComparingTo("0");
        assertThat(usuario.getSaldoEcoMoedas()).isEqualByComparingTo("10.00");
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void pagamentoSemSaldoEBloqueado() {
        when(comercioRepository.findForUpdateById(9L))
                .thenReturn(Optional.of(Comercio.builder().id(9L).nome("Padaria").build()));

        assertThatThrownBy(() -> service.pagar("ana", PagamentoRequest.builder()
                .idComercio(9L).valor(new BigDecimal("10.01")).tipo("VOUCHER").build()))
                .isInstanceOf(BusinessException.class);

        assertThat(usuario.getSaldoEcoMoedas()).isEqualByComparingTo("10.00");
    }

    @Test
    void transferenciaDebitaUsuarioECreditaComercio() {
        Comercio padaria = Comercio.builder().id(9L).nome("Padaria").build();
        when(comercioRepository.findForUpdateById(9L)).thenReturn(Optional.of(padaria));
        when(transacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var t = service.pagar("ana", PagamentoRequest.builder()
                .idComercio(9L).valor(new BigDecimal("4.00")).tipo("TRANSFERENCIA").build());

        assertThat(usuario.getSaldoEcoMoedas()).isEqualByComparingTo("6.00");
        assertThat(padaria.getSaldoRecebido()).isEqualByComparingTo("4.00");
        assertThat(t.getCodigoVoucher()).isNull();
    }
}
