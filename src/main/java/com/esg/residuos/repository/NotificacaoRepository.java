package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByLida(Boolean lida);
    List<Notificacao> findByPontoColetaId(Long pontoColetaId);
}
