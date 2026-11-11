package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.PontoColeta;
import com.esg.residuos.domain.enums.StatusPonto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {
    List<PontoColeta> findByStatus(StatusPonto status);
    List<PontoColeta> findByTipoResiduoId(Long tipoResiduoId);
}
