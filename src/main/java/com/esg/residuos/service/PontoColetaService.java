package com.esg.residuos.service;

import com.esg.residuos.domain.entity.PontoColeta;
import com.esg.residuos.domain.entity.TipoResiduo;
import com.esg.residuos.domain.enums.StatusPonto;
import com.esg.residuos.dto.request.PontoColetaRequest;
import com.esg.residuos.dto.response.PontoColetaResponse;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PontoColetaService {

    private final PontoColetaRepository repository;
    private final TipoResiduoService tipoResiduoService;

    @Transactional(readOnly = true)
    public List<PontoColetaResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PontoColetaResponse> listarPorStatus(StatusPonto status) {
        return repository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PontoColetaResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public PontoColeta buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta nao encontrado com id " + id));
    }

    public PontoColetaResponse criar(PontoColetaRequest request) {
        TipoResiduo tipoResiduo = tipoResiduoService.buscarEntidade(request.getIdTipoResiduo());
        PontoColeta entidade = PontoColeta.builder()
                .tipoResiduo(tipoResiduo)
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .endereco(request.getEndereco())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cep(request.getCep())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .capacidadeKg(request.getCapacidadeKg())
                .quantidadeAtualKg(BigDecimal.ZERO)
                .percentualOcupacao(BigDecimal.ZERO)
                .limiteAlertaPercentual(request.getLimiteAlertaPercentual() == null
                        ? new BigDecimal("80") : request.getLimiteAlertaPercentual())
                .status(StatusPonto.DISPONIVEL)
                .ativo(Boolean.TRUE)
                .build();
        return toResponse(repository.save(entidade));
    }

    public PontoColetaResponse atualizar(Long id, PontoColetaRequest request) {
        PontoColeta entidade = buscarEntidade(id);
        TipoResiduo tipoResiduo = tipoResiduoService.buscarEntidade(request.getIdTipoResiduo());
        entidade.setTipoResiduo(tipoResiduo);
        entidade.setNome(request.getNome());
        entidade.setDescricao(request.getDescricao());
        entidade.setEndereco(request.getEndereco());
        entidade.setBairro(request.getBairro());
        entidade.setCidade(request.getCidade());
        entidade.setEstado(request.getEstado());
        entidade.setCep(request.getCep());
        entidade.setLatitude(request.getLatitude());
        entidade.setLongitude(request.getLongitude());
        entidade.setCapacidadeKg(request.getCapacidadeKg());
        if (request.getLimiteAlertaPercentual() != null) {
            entidade.setLimiteAlertaPercentual(request.getLimiteAlertaPercentual());
        }
        return toResponse(repository.save(entidade));
    }

    public void excluir(Long id) {
        PontoColeta entidade = buscarEntidade(id);
        repository.delete(entidade);
    }

    PontoColetaResponse toResponse(PontoColeta p) {
        return PontoColetaResponse.builder()
                .id(p.getId())
                .idTipoResiduo(p.getTipoResiduo().getId())
                .nomeTipoResiduo(p.getTipoResiduo().getNome())
                .nome(p.getNome())
                .descricao(p.getDescricao())
                .endereco(p.getEndereco())
                .bairro(p.getBairro())
                .cidade(p.getCidade())
                .estado(p.getEstado())
                .cep(p.getCep())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .capacidadeKg(p.getCapacidadeKg())
                .quantidadeAtualKg(p.getQuantidadeAtualKg())
                .percentualOcupacao(p.getPercentualOcupacao())
                .limiteAlertaPercentual(p.getLimiteAlertaPercentual())
                .status(p.getStatus())
                .dataInstalacao(p.getDataInstalacao())
                .dataUltimaColeta(p.getDataUltimaColeta())
                .ativo(p.getAtivo())
                .build();
    }
}
