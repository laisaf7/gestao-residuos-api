package com.esg.residuos;

import com.esg.residuos.domain.entity.PontoColeta;
import com.esg.residuos.domain.entity.TipoResiduo;
import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.request.DescarteRequest;
import com.esg.residuos.dto.response.DescarteResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.repository.ColetaRepository;
import com.esg.residuos.repository.DescarteRepository;
import com.esg.residuos.repository.NotificacaoRepository;
import com.esg.residuos.repository.PontoColetaRepository;
import com.esg.residuos.service.DescarteService;
import com.esg.residuos.service.PontoColetaService;
import com.esg.residuos.service.TipoResiduoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios que validam a replicacao, na camada de servico, das
 * Atividades 1, 2 e 3 originalmente implementadas como triggers PL/SQL.
 */
@ExtendWith(MockitoExtension.class)
class DescarteServiceTest {

    @Mock private DescarteRepository descarteRepository;
    @Mock private PontoColetaRepository pontoColetaRepository;
    @Mock private NotificacaoRepository notificacaoRepository;
    @Mock private ColetaRepository coletaRepository;
    @Mock private TipoResiduoService tipoResiduoService;
    @Mock private PontoColetaService pontoColetaService;

    @InjectMocks
    private DescarteService descarteService;

    private TipoResiduo papel;
    private TipoResiduo plastico;
    private PontoColeta ponto;

    @BeforeEach
    void setUp() {
        papel = TipoResiduo.builder().id(1L).nome("PAPEL").build();
        plastico = TipoResiduo.builder().id(2L).nome("PLASTICO").destinacaoCorreta("Levar ao ponto de plastico").build();

        ponto = PontoColeta.builder()
                .id(10L)
                .nome("Ponto Central")
                .tipoResiduo(papel)
                .capacidadeKg(new BigDecimal("100.00"))
                .quantidadeAtualKg(new BigDecimal("70.00"))
                .percentualOcupacao(new BigDecimal("70.00"))
                .limiteAlertaPercentual(new BigDecimal("80.00"))
                .status(StatusPonto.DISPONIVEL)
                .ativo(Boolean.TRUE)
                .build();
    }

    @Test
    void deveBloquearDescarteEmPontoInativo() {
        ponto.setAtivo(Boolean.FALSE);
        when(pontoColetaRepository.findById(10L)).thenReturn(java.util.Optional.of(ponto));
        when(tipoResiduoService.buscarEntidade(1L)).thenReturn(papel);

        DescarteRequest request = DescarteRequest.builder()
                .idPontoColeta(10L).idTipoResiduo(1L).quantidadeKg(new BigDecimal("5")).build();

        assertThatThrownBy(() -> descarteService.registrar(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deveMarcarDescarteIncorretoEGerarNotificacao() {
        when(pontoColetaRepository.findById(10L)).thenReturn(java.util.Optional.of(ponto));
        when(tipoResiduoService.buscarEntidade(2L)).thenReturn(plastico);
        when(descarteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DescarteRequest request = DescarteRequest.builder()
                .idPontoColeta(10L).idTipoResiduo(2L).quantidadeKg(new BigDecimal("5")).build();

        DescarteResponse response = descarteService.registrar(request);

        assertThat(response.getDescarteCorreto()).isFalse();
        verify(notificacaoRepository, times(1)).save(any());
        verify(pontoColetaRepository, never()).save(any());
    }

    @Test
    void deveAtualizarPontoEGerarColetaAoCruzarLimite() {
        when(pontoColetaRepository.findById(10L)).thenReturn(java.util.Optional.of(ponto));
        when(tipoResiduoService.buscarEntidade(1L)).thenReturn(papel);
        when(descarteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(pontoColetaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(coletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // 70% + 15kg em 100kg de capacidade = 85% -> cruza o limite de 80%
        DescarteRequest request = DescarteRequest.builder()
                .idPontoColeta(10L).idTipoResiduo(1L).quantidadeKg(new BigDecimal("15")).build();

        DescarteResponse response = descarteService.registrar(request);

        assertThat(response.getDescarteCorreto()).isTrue();
        assertThat(ponto.getStatus()).isEqualTo(StatusPonto.ALERTA);
        verify(coletaRepository, times(1)).save(any());
        verify(notificacaoRepository, times(1)).save(any());
    }
}
