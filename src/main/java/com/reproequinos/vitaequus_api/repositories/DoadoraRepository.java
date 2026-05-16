package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Doadora;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoadoraRepository extends JpaRepository<Doadora, Long> {

    @EntityGraph(attributePaths = {"animal", "animal.propriedade", "animal.propriedade.veterinario"})
    Optional<Doadora> findByIdAndAnimalPropriedadeVeterinarioId(Long id, Long veterinarioId);

    boolean existsByAnimalId(Long animalId);

    @EntityGraph(attributePaths = {"animal", "animal.propriedade", "animal.propriedade.veterinario"})
    Optional<Doadora> findByAnimalId(Long animalId);

    @EntityGraph(attributePaths = {"animal", "animal.propriedade", "animal.propriedade.veterinario"})
    Optional<Doadora> findByAnimalIdAndAnimalPropriedadeVeterinarioId(Long animalId, Long veterinarioId);
}
