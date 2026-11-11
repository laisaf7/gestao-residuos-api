package com.esg.residuos.security;

import com.esg.residuos.domain.entity.Usuario;
import com.esg.residuos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .filter(Usuario::getAtivo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + usuario.getRole()))
                .build();
    }

    public String findRole(String username) {
        return usuarioRepository.findByUsernameIgnoreCase(username)
                .map(Usuario::getRole)
                .orElse("USER");
    }
}
