package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Produto;
import com.esg.residuos.dto.response.ProdutoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("select new com.esg.residuos.dto.response.ProdutoResponse("
            + "p.id, p.nome, p.descricao, p.precoReais, p.precoMoedas, a.id, a.nome, a.bairro, a.endereco, "
            + "t.nome, u.username, p.dataCriacao, p.dataAtualizacao, "
            + "case when p.imagem is null then false else true end) "
            + "from Produto p join p.atelie a join a.tipoResiduo t join a.artesao u "
            + "order by p.dataCriacao desc, p.id desc")
    List<ProdutoResponse> listarSemImagem();

    @Query("select p.imagem from Produto p where p.id = :id")
    Optional<byte[]> buscarImagem(@Param("id") Long id);
}
