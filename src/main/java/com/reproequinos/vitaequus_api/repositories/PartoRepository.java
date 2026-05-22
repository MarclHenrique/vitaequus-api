package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Parto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PartoRepository extends JpaRepository<Parto, Long>, JpaSpecificationExecutor<Parto> {

    @EntityGraph(attributePaths = {
            "gestacao", "doadora", "doadora.animal", "doadora.animal.proprietario",
            "propriedade", "veterinario", "potros", "potros.animalCriado"
    })
    Optional<Parto> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    boolean existsByGestacaoIdAndVeterinarioId(Long gestacaoId, Long veterinarioId);

    @Override
    @EntityGraph(attributePaths = {
            "gestacao", "doadora", "doadora.animal",
            "propriedade", "veterinario", "potros", "potros.animalCriado"
    })
    Page<Parto> findAll(Specification<Parto> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"potros"})
    List<Parto> findAll(Specification<Parto> spec, Sort sort);
}
