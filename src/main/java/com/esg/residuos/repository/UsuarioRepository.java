package com.esg.residuos.repository;

import com.esg.residuos.domain.entity.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameIgnoreCase(String username);

    // O lock serializa creditos e debitos da mesma carteira
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Usuario> findForUpdateByUsernameIgnoreCase(String username);
}
