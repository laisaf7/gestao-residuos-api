package com.esg.residuos.service;

import com.esg.residuos.domain.entity.TipoResiduo;
import com.esg.residuos.dto.request.TipoResiduoRequest;
import com.esg.residuos.dto.response.TipoResiduoResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.TipoResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoResiduoService {

    private final TipoResiduoRepository repository;

    @Transactional(readOnly = true)
    public List<TipoResiduoResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TipoResiduoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public TipoResiduo buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de residuo nao encontrado com id " + id));
    }

    public TipoResiduoResponse criar(TipoResiduoRequest request) {
        if (repository.existsByNomeIgnoreCase(request.getNome())) {
            throw new BusinessException("Ja existe um tipo de residuo cadastrado com o nome '" + request.getNome() + "'");
        }
        TipoResiduo entidade = TipoResiduo.builder()
                .nome(request.getNome())
                .categoria(request.getCategoria())
                .corIdentificacao(request.getCorIdentificacao())
                .destinacaoCorreta(request.getDestinacaoCorreta())
                .impactoAmbiental(request.getImpactoAmbiental())
                .ativo(request.getAtivo() == null ? Boolean.TRUE : request.getAtivo())
                .build();
        return toResponse(repository.save(entidade));
    }

    public TipoResiduoResponse atualizar(Long id, TipoResiduoRequest request) {
        TipoResiduo entidade = buscarEntidade(id);
        entidade.setNome(request.getNome());
        entidade.setCategoria(request.getCategoria());
        entidade.setCorIdentificacao(request.getCorIdentificacao());
        entidade.setDestinacaoCorreta(request.getDestinacaoCorreta());
        entidade.setImpactoAmbiental(request.getImpactoAmbiental());
        if (request.getAtivo() != null) {
            entidade.setAtivo(request.getAtivo());
        }
        return toResponse(repository.save(entidade));
    }

    public void excluir(Long id) {
        TipoResiduo entidade = buscarEntidade(id);
        repository.delete(entidade);
    }

    private TipoResiduoResponse toResponse(TipoResiduo t) {
        return TipoResiduoResponse.builder()
                .id(t.getId())
                .nome(t.getNome())
                .categoria(t.getCategoria())
                .corIdentificacao(t.getCorIdentificacao())
                .destinacaoCorreta(t.getDestinacaoCorreta())
                .impactoAmbiental(t.getImpactoAmbiental())
                .ativo(t.getAtivo())
                .build();
    }
}
