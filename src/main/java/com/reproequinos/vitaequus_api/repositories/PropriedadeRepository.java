package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Propriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
    List<Propriedade> findByAtivoTrueAndVeterinarioId(Long veterinarioId);

    Optional<Propriedade> findByIdAndAtivoTrueAndVeterinarioId(Long id, Long veterinarioId);
}