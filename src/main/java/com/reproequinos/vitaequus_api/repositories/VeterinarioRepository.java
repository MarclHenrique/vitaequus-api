package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByEmail(String email);

    boolean existsByEmail(String email);
}