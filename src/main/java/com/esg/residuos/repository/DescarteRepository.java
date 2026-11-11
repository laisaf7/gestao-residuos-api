package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Descarte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DescarteRepository extends JpaRepository<Descarte, Long> {
    List<Descarte> findByPontoColetaIdOrderByDataDescarteDesc(Long pontoColetaId);
    long countByDescarteCorreto(Boolean descarteCorreto);

    @Query("select sum(d.quantidadeKg) from Descarte d where d.usuario.id = :usuarioId and d.descarteCorreto = :correto")
    BigDecimal somarKgPorUsuario(@Param("usuarioId") Long usuarioId, @Param("correto") Boolean correto);
}
