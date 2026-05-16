package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.CheckupGestacional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckupGestacionalRepository extends JpaRepository<CheckupGestacional, Long> {

    @EntityGraph(attributePaths = {"gestacao", "gestacao.cobertura", "gestacao.cobertura.veterinario", "veterinario"})
    List<CheckupGestacional> findByGestacaoIdAndGestacaoCoberturaVeterinarioIdOrderByDataHoraAsc(
            Long gestacaoId,
            Long veterinarioId
    );

    @EntityGraph(attributePaths = {"gestacao", "gestacao.cobertura", "gestacao.cobertura.veterinario", "veterinario"})
    Optional<CheckupGestacional> findByIdAndGestacaoIdAndGestacaoCoberturaVeterinarioId(
            Long id,
            Long gestacaoId,
            Long veterinarioId
    );

    boolean existsByGestacaoIdAndGestacaoCoberturaVeterinarioId(Long gestacaoId, Long veterinarioId);

    @EntityGraph(attributePaths = {"gestacao", "gestacao.doadora", "gestacao.doadora.animal", "veterinario"})
    @Query("""
            select c
            from CheckupGestacional c
            where c.gestacao.doadora.animal.id = :animalId
              and c.gestacao.cobertura.veterinario.id = :veterinarioId
              and (:dataInicio is null or c.dataHora >= :dataInicio)
              and (:dataFim is null or c.dataHora <= :dataFim)
            order by c.dataHora desc
            """)
    List<CheckupGestacional> findTimelineByDoadoraAnimalAndVeterinario(
            @Param("animalId") Long animalId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
