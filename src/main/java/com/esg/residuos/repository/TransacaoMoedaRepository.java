package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.TransacaoMoeda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoMoedaRepository extends JpaRepository<TransacaoMoeda, Long> {
    List<TransacaoMoeda> findTop20ByUsuarioIdOrderByIdDesc(Long usuarioId);
}
