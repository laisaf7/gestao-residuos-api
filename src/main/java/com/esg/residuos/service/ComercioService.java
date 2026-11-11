package com.esg.residuos.service;

import com.esg.residuos.domain.entity.Comercio;
import com.esg.residuos.dto.response.ComercioResponse;
import com.esg.residuos.repository.ComercioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComercioService {

    private final ComercioRepository repository;

    public List<ComercioResponse> listar(String categoria) {
        List<Comercio> comercios = categoria == null
                ? repository.findByAtivoOrderByNomeAsc(Boolean.TRUE)
                : repository.findByCategoriaIgnoreCaseAndAtivoOrderByNomeAsc(categoria, Boolean.TRUE);
        return comercios.stream()
                .map(c -> ComercioResponse.builder()
                        .id(c.getId())
                        .nome(c.getNome())
                        .categoria(c.getCategoria())
                        .descricao(c.getDescricao())
                        .endereco(c.getEndereco())
                        .bairro(c.getBairro())
                        .build())
                .toList();
    }
}
