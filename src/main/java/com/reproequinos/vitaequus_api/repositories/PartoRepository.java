package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Parto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PartoRepository extends JpaRepository<Parto, Long> {

    @EntityGraph(attributePaths = {
            "gestacao", "doadora", "doadora.animal", "doadora.animal.proprietario",
            "propriedade", "veterinario", "potros", "potros.animalCriado"
    })
    Optional<Parto> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    boolean existsByGestacaoIdAndVeterinarioId(Long gestacaoId, Long veterinarioId);

    @EntityGraph(attributePaths = {
            "gestacao", "doadora", "doadora.animal",
            "propriedade", "veterinario", "potros", "potros.animalCriado"
    })
    @Query("""
            select p
            from Parto p
            where p.veterinario.id = :veterinarioId
              and (:gestacaoId is null or p.gestacao.id = :gestacaoId)
              and (:doadoraId is null or p.doadora.id = :doadoraId)
              and (:propriedadeId is null or p.propriedade.id = :propriedadeId)
              and (:dataInicio is null or p.dataHora >= :dataInicio)
              and (:dataFim is null or p.dataHora <= :dataFim)
            """)
    Page<Parto> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("gestacaoId") Long gestacaoId,
            @Param("doadoraId") Long doadoraId,
            @Param("propriedadeId") Long propriedadeId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"potros"})
    @Query("""
            select p
            from Parto p
            where p.doadora.animal.id = :animalId
              and p.veterinario.id = :veterinarioId
              and (:dataInicio is null or p.dataHora >= :dataInicio)
              and (:dataFim is null or p.dataHora <= :dataFim)
            order by p.dataHora desc
            """)
    List<Parto> findTimelineByDoadoraAnimalAndVeterinario(
            @Param("animalId") Long animalId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
