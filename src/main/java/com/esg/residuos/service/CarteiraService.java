package com.esg.residuos.service;

import com.esg.residuos.domain.entity.*;
import com.esg.residuos.domain.enums.TipoTransacao;
import com.esg.residuos.dto.request.DescarteRequest;
import com.esg.residuos.dto.request.EntregaQrRequest;
import com.esg.residuos.dto.request.PagamentoRequest;
import com.esg.residuos.dto.response.*;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/** Regras da carteira de EcoMoedas: geracao e resgate de QR Code, saldo e pagamentos. */
@Service
@RequiredArgsConstructor
@Transactional
public class CarteiraService {

    private final UsuarioRepository usuarioRepository;
    private final EntregaQrRepository entregaQrRepository;
    private final TransacaoMoedaRepository transacaoRepository;
    private final ComercioRepository comercioRepository;
    private final DescarteRepository descarteRepository;
    private final DescarteService descarteService;
    private final PontoColetaService pontoColetaService;
    private final TipoResiduoService tipoResiduoService;

    @Transactional(readOnly = true)
    public CarteiraResponse consultar(String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
        BigDecimal kg = descarteRepository.somarKgPorUsuario(usuario.getId(), Boolean.TRUE);
        return CarteiraResponse.builder()
                .saldo(usuario.getSaldoEcoMoedas())
                .kgDesviados(kg == null ? BigDecimal.ZERO : kg)
                .transacoes(transacaoRepository.findTop20ByUsuarioIdOrderByIdDesc(usuario.getId())
                        .stream().map(this::toResponse).toList())
                .build();
    }

    public EntregaQrResponse gerarQr(EntregaQrRequest request) {
        PontoColeta ponto = pontoColetaService.buscarEntidade(request.getIdPontoColeta());
        TipoResiduo tipo = tipoResiduoService.buscarEntidade(request.getIdTipoResiduo());
        if (!ponto.getTipoResiduo().getId().equals(tipo.getId())) {
            throw new BusinessException("O ponto '" + ponto.getNome() + "' nao aceita o residuo " + tipo.getNome());
        }
        EntregaQr entrega = entregaQrRepository.save(EntregaQr.builder()
                .codigo(UUID.randomUUID().toString())
                .pontoColeta(ponto)
                .tipoResiduo(tipo)
                .quantidadeKg(request.getQuantidadeKg())
                .build());
        return EntregaQrResponse.builder()
                .codigo(entrega.getCodigo())
                .nomePontoColeta(ponto.getNome())
                .nomeTipoResiduo(tipo.getNome())
                .quantidadeKg(entrega.getQuantidadeKg())
                .moedas(converter(entrega))
                .build();
    }

    public ResgateResponse resgatar(String username, String codigo) {
        EntregaQr entrega = entregaQrRepository.findForUpdateByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("QR Code invalido"));
        if (entrega.getUsuarioResgate() != null) {
            throw new BusinessException("Este QR Code ja foi utilizado");
        }
        Usuario usuario = buscarComLock(username);

        DescarteResponse descarte = descarteService.registrar(DescarteRequest.builder()
                .idPontoColeta(entrega.getPontoColeta().getId())
                .idTipoResiduo(entrega.getTipoResiduo().getId())
                .quantidadeKg(entrega.getQuantidadeKg())
                .origem("App EcoCiclo")
                .build(), usuario);

        entrega.setUsuarioResgate(usuario);
        entrega.setDataResgate(LocalDateTime.now());

        boolean correto = Boolean.TRUE.equals(descarte.getDescarteCorreto());
        BigDecimal moedas = correto ? converter(entrega) : BigDecimal.ZERO;
        if (correto) {
            usuario.setSaldoEcoMoedas(usuario.getSaldoEcoMoedas().add(moedas));
            transacaoRepository.save(TransacaoMoeda.builder()
                    .usuario(usuario)
                    .tipo(TipoTransacao.CREDITO)
                    .valor(moedas)
                    .descricao("Entrega de " + entrega.getQuantidadeKg() + " kg em " + descarte.getNomePontoColeta())
                    .build());
        }
        return ResgateResponse.builder()
                .nomePontoColeta(descarte.getNomePontoColeta())
                .nomeTipoResiduo(descarte.getNomeTipoResiduo())
                .quantidadeKg(entrega.getQuantidadeKg())
                .descarteCorreto(correto)
                .moedasCreditadas(moedas)
                .saldo(usuario.getSaldoEcoMoedas())
                .build();
    }

    public TransacaoResponse pagar(String username, PagamentoRequest request) {
        Usuario usuario = buscarComLock(username);
        Comercio comercio = comercioRepository.findForUpdateById(request.getIdComercio())
                .filter(c -> Boolean.TRUE.equals(c.getAtivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Comercio nao encontrado"));
        if (usuario.getSaldoEcoMoedas().compareTo(request.getValor()) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }
        usuario.setSaldoEcoMoedas(usuario.getSaldoEcoMoedas().subtract(request.getValor()));

        TipoTransacao tipo = TipoTransacao.valueOf(request.getTipo());
        boolean voucher = tipo == TipoTransacao.VOUCHER;
        if (!voucher) {
            comercio.setSaldoRecebido(comercio.getSaldoRecebido().add(request.getValor()));
        }
        return toResponse(transacaoRepository.save(TransacaoMoeda.builder()
                .usuario(usuario)
                .tipo(tipo)
                .valor(request.getValor())
                .descricao((voucher ? "Voucher para " : "Transferencia para ") + comercio.getNome())
                .comercio(comercio)
                .codigoVoucher(voucher ? UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase() : null)
                .build()));
    }

    private BigDecimal converter(EntregaQr entrega) {
        return entrega.getQuantidadeKg()
                .multiply(entrega.getTipoResiduo().getMoedasPorKg())
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Usuario buscarComLock(String username) {
        return usuarioRepository.findForUpdateByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    private TransacaoResponse toResponse(TransacaoMoeda t) {
        return TransacaoResponse.builder()
                .id(t.getId())
                .tipo(t.getTipo())
                .valor(t.getValor())
                .descricao(t.getDescricao())
                .codigoVoucher(t.getCodigoVoucher())
                .dataTransacao(t.getDataTransacao())
                .build();
    }
}
