package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Cobertura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CoberturaRepository extends JpaRepository<Cobertura, Long> {

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "produtor", "produtor.animal",
            "propriedade", "veterinario"
    })
    Optional<Cobertura> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "produtor", "produtor.animal",
            "propriedade", "veterinario"
    })
    @Query("""
            select c
            from Cobertura c
            where c.veterinario.id = :veterinarioId
              and (:doadoraAnimalId is null or c.doadora.animal.id = :doadoraAnimalId)
              and (:produtorAnimalId is null or c.produtor.animal.id = :produtorAnimalId)
              and (:propriedadeId is null or c.propriedade.id = :propriedadeId)
              and (:dataInicio is null or c.dataHora >= :dataInicio)
              and (:dataFim is null or c.dataHora <= :dataFim)
            """)
    Page<Cobertura> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("doadoraAnimalId") Long doadoraAnimalId,
            @Param("produtorAnimalId") Long produtorAnimalId,
            @Param("propriedadeId") Long propriedadeId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "produtor", "produtor.animal",
            "propriedade", "veterinario"
    })
    @Query("""
            select c
            from Cobertura c
            where c.veterinario.id = :veterinarioId
              and (c.doadora.animal.id = :animalId or c.produtor.animal.id = :animalId)
              and (:dataInicio is null or c.dataHora >= :dataInicio)
              and (:dataFim is null or c.dataHora <= :dataFim)
            order by c.dataHora desc
            """)
    List<Cobertura> findTimelineByAnimalAndVeterinario(
            @Param("animalId") Long animalId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
