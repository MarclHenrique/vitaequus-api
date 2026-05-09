package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.AtendimentoClinico;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AtendimentoClinicoRepository extends JpaRepository<AtendimentoClinico, Long> {

    @EntityGraph(attributePaths = {
            "animal",
            "propriedade",
            "veterinario",
            "medicacoes",
            "medicacoes.insumo"
    })
    Optional<AtendimentoClinico> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario"})
    @Query("""
            select a
            from AtendimentoClinico a
            where a.veterinario.id = :veterinarioId
              and (:animalId is null or a.animal.id = :animalId)
              and (:tipoAtendimento is null or a.tipoAtendimento = :tipoAtendimento)
              and (:dataInicio is null or a.dataHora >= :dataInicio)
              and (:dataFim is null or a.dataHora <= :dataFim)
            """)
    Page<AtendimentoClinico> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("animalId") Long animalId,
            @Param("tipoAtendimento") TipoAtendimento tipoAtendimento,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario"})
    @Query("""
            select a
            from AtendimentoClinico a
            where a.animal.id = :animalId
              and a.veterinario.id = :veterinarioId
              and (:dataInicio is null or a.dataHora >= :dataInicio)
              and (:dataFim is null or a.dataHora <= :dataFim)
            order by a.dataHora desc
            """)
    List<AtendimentoClinico> findTimelineByAnimalAndVeterinario(
            @Param("animalId") Long animalId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
