package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Coleta;
import com.esg.residuos.domain.enums.StatusColeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColetaRepository extends JpaRepository<Coleta, Long> {
    List<Coleta> findByPontoColetaId(Long pontoColetaId);
    List<Coleta> findByStatus(StatusColeta status);
}
