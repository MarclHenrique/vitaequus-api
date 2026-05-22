package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GestacaoRepository extends JpaRepository<Gestacao, Long>, JpaSpecificationExecutor<Gestacao> {

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "cobertura", "cobertura.propriedade", "cobertura.veterinario"
    })
    Optional<Gestacao> findByIdAndCoberturaVeterinarioId(Long id, Long veterinarioId);

    boolean existsByCoberturaIdAndCoberturaVeterinarioId(Long coberturaId, Long veterinarioId);

    @Override
    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "cobertura", "cobertura.propriedade", "cobertura.veterinario"
    })
    Page<Gestacao> findAll(Specification<Gestacao> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal", "doadora.animal.propriedade",
            "cobertura", "cobertura.propriedade", "cobertura.veterinario"
    })
    List<Gestacao> findAll(Specification<Gestacao> spec, Sort sort);
}
