package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Artigo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtigoRepository extends JpaRepository<Artigo, Long> {
    List<Artigo> findAllByOrderByDataCriacaoDesc();

    List<Artigo> findByMaterialIgnoreCaseOrderByDataCriacaoDesc(String material);
}
