package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.CheckupGestacional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckupGestacionalRepository extends JpaRepository<CheckupGestacional, Long>, JpaSpecificationExecutor<CheckupGestacional> {

    @EntityGraph(attributePaths = {
            "gestacao", "gestacao.doadora", "gestacao.doadora.animal",
            "gestacao.cobertura", "gestacao.cobertura.propriedade", "gestacao.cobertura.veterinario",
            "veterinario"
    })
    List<CheckupGestacional> findByGestacaoIdAndGestacaoCoberturaVeterinarioIdOrderByDataHoraAsc(
            Long gestacaoId,
            Long veterinarioId
    );

    @EntityGraph(attributePaths = {
            "gestacao", "gestacao.doadora", "gestacao.doadora.animal",
            "gestacao.cobertura", "gestacao.cobertura.propriedade", "gestacao.cobertura.veterinario",
            "veterinario"
    })
    Optional<CheckupGestacional> findByIdAndGestacaoIdAndGestacaoCoberturaVeterinarioId(
            Long id,
            Long gestacaoId,
            Long veterinarioId
    );

    boolean existsByGestacaoIdAndGestacaoCoberturaVeterinarioId(Long gestacaoId, Long veterinarioId);

    @Override
    @EntityGraph(attributePaths = {
            "gestacao", "gestacao.doadora", "gestacao.doadora.animal",
            "gestacao.cobertura", "gestacao.cobertura.propriedade", "gestacao.cobertura.veterinario",
            "veterinario"
    })
    Page<CheckupGestacional> findAll(Specification<CheckupGestacional> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "gestacao", "gestacao.doadora", "gestacao.doadora.animal",
            "gestacao.cobertura", "gestacao.cobertura.propriedade", "gestacao.cobertura.veterinario",
            "veterinario"
    })
    List<CheckupGestacional> findAll(Specification<CheckupGestacional> spec, Sort sort);
}
