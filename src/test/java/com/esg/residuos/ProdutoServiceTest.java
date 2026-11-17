package com.esg.residuos;

import com.esg.residuos.domain.entity.*;
import com.esg.residuos.domain.enums.TipoTransacao;
import com.esg.residuos.dto.request.ProdutoRequest;
import com.esg.residuos.dto.response.TransacaoResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.repository.*;
import com.esg.residuos.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
class ProdutoServiceTest {

    @Mock private ProdutoRepository repository;
    @Mock private PontoColetaRepository pontoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private TransacaoMoedaRepository transacaoRepository;
    @Mock private EntregaQrRepository entregaQrRepository;
    @InjectMocks private ProdutoService service;

    private Usuario artesa;
    private Usuario cliente;
    private PontoColeta atelie;
    private Produto vaso;

    @BeforeEach
    void setUp() {
        artesa = Usuario.builder().id(1L).username("bia").role("ARTESAO").saldoEcoMoedas(new BigDecimal("5.00")).build();
        cliente = Usuario.builder().id(2L).username("ana").role("USER").saldoEcoMoedas(new BigDecimal("100.00")).build();
        TipoResiduo plastico = TipoResiduo.builder().id(2L).nome("PLASTICO").build();
        atelie = PontoColeta.builder().id(5L).nome("Atelie da Bia").tipoResiduo(plastico).artesao(artesa).build();
        vaso = Produto.builder().id(7L).atelie(atelie).nome("Vaso").precoReais(new BigDecimal("25"))
                .precoMoedas(new BigDecimal("60.00")).build();
        when(repository.findById(7L)).thenReturn(Optional.of(vaso));
        when(usuarioRepository.findForUpdateByUsernameIgnoreCase("bia")).thenReturn(Optional.of(artesa));
        when(usuarioRepository.findForUpdateByUsernameIgnoreCase("ana")).thenReturn(Optional.of(cliente));
        when(transacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void compraMoveMoedasDoClienteParaOArtesao() {
        TransacaoResponse t = service.comprar(7L, "ana");

        assertThat(cliente.getSaldoEcoMoedas()).isEqualByComparingTo("40.00");
        assertThat(artesa.getSaldoEcoMoedas()).isEqualByComparingTo("65.00");
        assertThat(t.getTipo()).isEqualTo(TipoTransacao.COMPRA);
        assertThat(t.getCodigoVoucher()).hasSize(12);

        ArgumentCaptor<TransacaoMoeda> salvas = ArgumentCaptor.forClass(TransacaoMoeda.class);
        verify(transacaoRepository, times(2)).save(salvas.capture());
        assertThat(salvas.getAllValues()).extracting(TransacaoMoeda::getTipo)
                .containsExactly(TipoTransacao.VENDA, TipoTransacao.COMPRA);
    }

    @Test
    void cupomDeQuemEntregouNoAtelieDaMetadeEESeGasta() {
        EntregaQr entrega = EntregaQr.builder().id(3L).descontoDisponivel(true).build();
        when(entregaQrRepository.findFirstByUsuarioResgateIdAndPontoColetaIdAndDescontoDisponivelOrderByIdAsc(2L, 5L, true))
                .thenReturn(Optional.of(entrega));

        TransacaoResponse t = service.comprar(7L, "ana");

        assertThat(t.getValor()).isEqualByComparingTo("30.00");
        assertThat(cliente.getSaldoEcoMoedas()).isEqualByComparingTo("70.00");
        assertThat(artesa.getSaldoEcoMoedas()).isEqualByComparingTo("35.00");
        assertThat(entrega.getDescontoDisponivel()).isFalse();
    }

    @Test
    void saldoInsuficienteNaoMexeNasCarteiras() {
        cliente.setSaldoEcoMoedas(new BigDecimal("59.99"));

        assertThatThrownBy(() -> service.comprar(7L, "ana")).isInstanceOf(BusinessException.class).hasMessage("Saldo insuficiente");
        assertThat(artesa.getSaldoEcoMoedas()).isEqualByComparingTo("5.00");
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void artesaoNaoCompraOProprioProduto() {
        assertThatThrownBy(() -> service.comprar(7L, "BIA")).isInstanceOf(BusinessException.class);
    }

    @Test
    void produtoSoEmReaisNaoECompradoNoApp() {
        vaso.setPrecoMoedas(null);
        assertThatThrownBy(() -> service.comprar(7L, "ana")).isInstanceOf(BusinessException.class);
        assertThat(cliente.getSaldoEcoMoedas()).isEqualByComparingTo("100.00");
    }

    @Test
    void publicarUsaAtelieDeQuemEstaLogado() {
        when(pontoRepository.findByArtesaoUsernameIgnoreCase("bia")).thenReturn(Optional.of(atelie));

        var r = service.criar("bia", ProdutoRequest.builder().nome("  Bolsa  ").descricao(" ").precoReais(BigDecimal.TEN).build());

        assertThat(r.getNome()).isEqualTo("Bolsa");
        assertThat(r.getDescricao()).isNull();
        assertThat(r.getNomeAtelie()).isEqualTo("Atelie da Bia");
        assertThat(r.getMaterial()).isEqualTo("PLASTICO");
    }

    @Test
    void quemNaoEArtesaoNaoPublica() {
        assertThatThrownBy(() -> service.criar("ana", ProdutoRequest.builder().nome("x").precoReais(BigDecimal.TEN).build()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void outroArtesaoNaoEditaNemExclui() {
        ProdutoRequest pedido = ProdutoRequest.builder().nome("x").precoReais(BigDecimal.TEN).build();
        assertThatThrownBy(() -> service.editar(7L, "carla", pedido)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> service.excluir(7L, "carla", false)).isInstanceOf(AccessDeniedException.class);
        service.excluir(7L, "admin", true);
        verify(repository).delete(vaso);
    }
}
