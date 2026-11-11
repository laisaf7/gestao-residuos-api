package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.EntregaQr;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface EntregaQrRepository extends JpaRepository<EntregaQr, Long> {
    // O lock impede que dois resgates simultaneos usem o mesmo QR Code
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EntregaQr> findForUpdateByCodigo(String codigo);
}
