package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Comercio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ComercioRepository extends JpaRepository<Comercio, Long> {
    List<Comercio> findByAtivoOrderByNomeAsc(Boolean ativo);

    List<Comercio> findByCategoriaIgnoreCaseAndAtivoOrderByNomeAsc(String categoria, Boolean ativo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Comercio> findForUpdateById(Long id);
}
