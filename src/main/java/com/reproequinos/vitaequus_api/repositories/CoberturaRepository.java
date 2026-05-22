package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Cobertura;
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

public interface CoberturaRepository extends JpaRepository<Cobertura, Long>, JpaSpecificationExecutor<Cobertura> {

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "produtor", "produtor.animal",
            "propriedade", "veterinario"
    })
    Optional<Cobertura> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @Override
    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "produtor", "produtor.animal",
            "propriedade", "veterinario"
    })
    Page<Cobertura> findAll(Specification<Cobertura> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal", "doadora.animal.propriedade",
            "produtor", "produtor.animal", "propriedade", "veterinario"
    })
    List<Cobertura> findAll(Specification<Cobertura> spec, Sort sort);
}
