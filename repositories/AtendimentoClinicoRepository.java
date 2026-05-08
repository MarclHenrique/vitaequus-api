package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.AtendimentoClinico;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AtendimentoClinicoRepository extends JpaRepository<AtendimentoClinico, Long> {

    @EntityGraph(attributePaths = {"animal", "veterinario", "propriedade"})
    Page<AtendimentoClinico> findByVeterinarioId(Long veterinarioId, Pageable pageable);

    @EntityGraph(attributePaths = {"animal", "veterinario", "propriedade"})
    Optional<AtendimentoClinico> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @EntityGraph(attributePaths = {"animal", "veterinario", "propriedade"})
    @Query("""
        SELECT a FROM AtendimentoClinico a
        WHERE a.veterinario.id = :veterinarioId
        AND (:animalId IS NULL OR a.animal.id = :animalId)
        AND (:tipo IS NULL OR a.tipoAtendimento = :tipo)
        AND (:dataInicio IS NULL OR a.dataHora >= :dataInicio)
        AND (:dataFim IS NULL OR a.dataHora <= :dataFim)
        """)
    Page<AtendimentoClinico> findComFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("animalId") Long animalId,
            @Param("tipo") TipoAtendimento tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );
}
