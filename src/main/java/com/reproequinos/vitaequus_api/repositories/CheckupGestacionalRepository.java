package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.CheckupGestacional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckupGestacionalRepository extends JpaRepository<CheckupGestacional, Long> {

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
    @Query("""
            select c
            from CheckupGestacional c
            where c.gestacao.id = :gestacaoId
              and c.gestacao.cobertura.veterinario.id = :veterinarioId
              and (:resultado is null or lower(c.resultado) like lower(concat('%', :resultado, '%')))
              and (:dataInicio is null or c.dataHora >= :dataInicio)
              and (:dataFim is null or c.dataHora <= :dataFim)
            """)
    Page<CheckupGestacional> findByFiltros(
            @Param("gestacaoId") Long gestacaoId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("resultado") String resultado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "gestacao", "gestacao.doadora", "gestacao.doadora.animal",
            "gestacao.cobertura", "gestacao.cobertura.propriedade", "gestacao.cobertura.veterinario",
            "veterinario"
    })
    @Query("""
            select c
            from CheckupGestacional c
            where (c.gestacao.cobertura.veterinario.id = :veterinarioId or c.veterinario.id = :veterinarioId)
              and (:gestacaoId is null or c.gestacao.id = :gestacaoId)
              and (:resultado is null or lower(c.resultado) like lower(concat('%', :resultado, '%')))
              and (:dataInicio is null or c.dataHora >= :dataInicio)
              and (:dataFim is null or c.dataHora <= :dataFim)
            """)
    Page<CheckupGestacional> findByFiltrosGlobais(
            @Param("veterinarioId") Long veterinarioId,
            @Param("gestacaoId") Long gestacaoId,
            @Param("resultado") String resultado,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
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

    @EntityGraph(attributePaths = {
            "gestacao", "gestacao.doadora", "gestacao.doadora.animal",
            "gestacao.cobertura", "gestacao.cobertura.propriedade", "gestacao.cobertura.veterinario",
            "veterinario"
    })
    @Query("""
            select c
            from CheckupGestacional c
            where c.gestacao.cobertura.veterinario.id = :veterinarioId
              and (:propriedadeId is null or c.gestacao.cobertura.propriedade.id = :propriedadeId)
              and c.dataHora >= :dataInicio
              and c.dataHora <= :dataFim
            order by c.dataHora asc
            """)
    List<CheckupGestacional> findProximosDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("propriedadeId") Long propriedadeId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
