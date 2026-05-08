package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.repositories.VeterinarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class VeterinarioUserDetailsService implements UserDetailsService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioUserDetailsService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Veterinario veterinario = veterinarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Veterinário não encontrado: " + email));

        return new User(
                veterinario.getEmail(),
                veterinario.getPassword(),
                Collections.emptyList() // sem roles por enquanto
        );
    }
}