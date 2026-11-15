package com.esg.residuos.service;

import com.esg.residuos.domain.entity.Artigo;
import com.esg.residuos.dto.request.ArtigoRequest;
import com.esg.residuos.dto.response.ArtigoResponse;
import com.esg.residuos.repository.ArtigoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtigoService {

    private final ArtigoRepository repository;

    public List<ArtigoResponse> listar(String material) {
        List<Artigo> artigos = material == null
                ? repository.findAllByOrderByDataCriacaoDesc()
                : repository.findByMaterialIgnoreCaseOrderByDataCriacaoDesc(material);
        return artigos.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ArtigoResponse criar(String autor, ArtigoRequest request) {
        Artigo artigo = repository.save(Artigo.builder()
                .titulo(request.getTitulo().trim())
                .material(request.getMaterial())
                .conteudo(request.getConteudo().trim())
                .autor(autor)
                .build());
        return toResponse(artigo);
    }

    private ArtigoResponse toResponse(Artigo a) {
        return ArtigoResponse.builder()
                .id(a.getId())
                .titulo(a.getTitulo())
                .material(a.getMaterial())
                .conteudo(a.getConteudo())
                .autor(a.getAutor())
                .dataCriacao(a.getDataCriacao())
                .build();
    }
}
