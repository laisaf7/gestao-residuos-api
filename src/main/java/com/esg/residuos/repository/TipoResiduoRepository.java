package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.TipoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long> {
    Optional<TipoResiduo> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
