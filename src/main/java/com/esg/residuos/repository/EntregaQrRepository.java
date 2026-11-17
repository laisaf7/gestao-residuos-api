package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.EntregaQr;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EntregaQrRepository extends JpaRepository<EntregaQr, Long> {
    // O lock impede que dois resgates simultaneos usem o mesmo QR Code
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EntregaQr> findForUpdateByCodigo(String codigo);

    /** Cupom de desconto mais antigo ainda nao usado, do usuario naquele atelie. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EntregaQr> findFirstByUsuarioResgateIdAndPontoColetaIdAndDescontoDisponivelOrderByIdAsc(
            Long usuarioId, Long pontoColetaId, Boolean descontoDisponivel);

    @Query("select distinct e.pontoColeta.id from EntregaQr e where e.usuarioResgate.id = :usuarioId and e.descontoDisponivel = :disponivel")
    List<Long> ateliesComDesconto(@Param("usuarioId") Long usuarioId, @Param("disponivel") Boolean disponivel);
}
