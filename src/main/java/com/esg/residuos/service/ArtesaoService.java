package com.esg.residuos.service;

import com.esg.residuos.domain.entity.PontoColeta;
import com.esg.residuos.domain.entity.Usuario;
import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.request.PontoColetaRequest;
import com.esg.residuos.dto.response.PontoColetaResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.PontoColetaRepository;
import com.esg.residuos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Usuario comum vira artesao: ganha um atelie, que funciona como ponto de coleta pequeno. */
@Service
@RequiredArgsConstructor
@Transactional
public class ArtesaoService {

    /** Atelie e menor que um ecoponto. */
    static final BigDecimal CAPACIDADE_MAXIMA_KG = new BigDecimal("100");

    /** Incentivo de quem entrega no atelie: +50% de EcoMoedas e cupom de 50% num produto de la. */
    public static final int INCENTIVO_PERCENTUAL = 50;

    static BigDecimal multiplicadorBonus() {
        return BigDecimal.ONE.add(BigDecimal.valueOf(INCENTIVO_PERCENTUAL, 2));
    }

    static BigDecimal multiplicadorDesconto() {
        return BigDecimal.ONE.subtract(BigDecimal.valueOf(INCENTIVO_PERCENTUAL, 2));
    }

    private final UsuarioRepository usuarioRepository;
    private final PontoColetaRepository pontoRepository;
    private final PontoColetaService pontoColetaService;

    public PontoColetaResponse cadastrar(String username, PontoColetaRequest request) {
        if (request.getCapacidadeKg().compareTo(CAPACIDADE_MAXIMA_KG) > 0) {
            throw new BusinessException("O atelie recebe no maximo " + CAPACIDADE_MAXIMA_KG + " kg");
        }
        Usuario usuario = usuarioRepository.findForUpdateByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
        if (!"USER".equals(usuario.getRole())) {
            throw new BusinessException("Apenas usuarios comuns podem se cadastrar como artesao");
        }
        PontoColeta atelie = pontoColetaService.montar(request);
        atelie.setArtesao(usuario);
        usuario.setRole("ARTESAO");
        return pontoColetaService.toResponse(pontoRepository.save(atelie));
    }

    @Transactional(readOnly = true)
    public PontoColetaResponse meuAtelie(String username) {
        return pontoColetaService.toResponse(buscarAtelie(username));
    }

    /** O artesao usou o material: zera a ocupacao (ecopontos sao esvaziados pela coleta). */
    public PontoColetaResponse esvaziar(String username) {
        PontoColeta atelie = buscarAtelie(username);
        atelie.setQuantidadeAtualKg(BigDecimal.ZERO);
        atelie.setPercentualOcupacao(BigDecimal.ZERO);
        atelie.setStatus(StatusPonto.DISPONIVEL);
        atelie.setDataUltimaColeta(LocalDate.now());
        return pontoColetaService.toResponse(atelie);
    }

    private PontoColeta buscarAtelie(String username) {
        return pontoRepository.findByArtesaoUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Voce ainda nao tem um atelie"));
    }
}
