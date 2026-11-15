package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Artigo;
import com.esg.residuos.dto.response.ArtigoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtigoRepository extends JpaRepository<Artigo, Long> {

    String PROJECAO = "select new com.esg.residuos.dto.response.ArtigoResponse("
            + "a.id, a.titulo, a.material, a.conteudo, a.autor, a.dataCriacao, a.dataAtualizacao, "
            + "case when a.imagem is null then false else true end) from Artigo a ";

    @Query(PROJECAO + "order by a.dataCriacao desc, a.id desc")
    List<ArtigoResponse> listarSemImagem();

    @Query(PROJECAO + "where lower(a.material) = lower(:material) order by a.dataCriacao desc, a.id desc")
    List<ArtigoResponse> listarSemImagemPorMaterial(@Param("material") String material);

    @Query("select a.imagem from Artigo a where a.id = :id")
    Optional<byte[]> buscarImagem(@Param("id") Long id);
}
