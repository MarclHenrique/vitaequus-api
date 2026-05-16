package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Produtor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutorRepository extends JpaRepository<Produtor, Long> {

    @EntityGraph(attributePaths = {"animal", "animal.propriedade", "animal.propriedade.veterinario"})
    Optional<Produtor> findByIdAndAnimalPropriedadeVeterinarioId(Long id, Long veterinarioId);

    boolean existsByAnimalId(Long animalId);

    @EntityGraph(attributePaths = {"animal", "animal.propriedade", "animal.propriedade.veterinario"})
    Optional<Produtor> findByAnimalId(Long animalId);

    @EntityGraph(attributePaths = {"animal", "animal.propriedade", "animal.propriedade.veterinario"})
    Optional<Produtor> findByAnimalIdAndAnimalPropriedadeVeterinarioId(Long animalId, Long veterinarioId);
}
