package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.ExameReprodutivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExameReprodutivoRepository extends JpaRepository<ExameReprodutivo, Long> {

    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario", "insumo"})
    Optional<ExameReprodutivo> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario", "insumo"})
    @Query("""
            select e
            from ExameReprodutivo e
            where e.veterinario.id = :veterinarioId
              and (:animalId is null or e.animal.id = :animalId)
              and (:propriedadeId is null or e.propriedade.id = :propriedadeId)
              and (:dataInicio is null or e.dataHora >= :dataInicio)
              and (:dataFim is null or e.dataHora <= :dataFim)
            """)
    Page<ExameReprodutivo> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("animalId") Long animalId,
            @Param("propriedadeId") Long propriedadeId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario", "insumo"})
    @Query("""
            select e
            from ExameReprodutivo e
            where e.animal.id = :animalId
              and e.veterinario.id = :veterinarioId
              and (:dataInicio is null or e.dataHora >= :dataInicio)
              and (:dataFim is null or e.dataHora <= :dataFim)
            order by e.dataHora desc
            """)
    List<ExameReprodutivo> findTimelineByAnimalAndVeterinario(
            @Param("animalId") Long animalId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @EntityGraph(attributePaths = {"animal", "propriedade"})
    @Query("""
            select e
            from ExameReprodutivo e
            where e.veterinario.id = :veterinarioId
              and e.animal.id in :animalIds
              and e.dataHora <= :dataReferenciaFim
              and not exists (
                  select 1
                  from ExameReprodutivo posterior
                  where posterior.veterinario.id = :veterinarioId
                    and posterior.animal.id = e.animal.id
                    and posterior.dataHora <= :dataReferenciaFim
                    and (
                        posterior.dataHora > e.dataHora
                        or (posterior.dataHora = e.dataHora and posterior.id > e.id)
                    )
              )
            """)
    List<ExameReprodutivo> findUltimosExamesPorAnimais(
            @Param("veterinarioId") Long veterinarioId,
            @Param("animalIds") Collection<Long> animalIds,
            @Param("dataReferenciaFim") LocalDateTime dataReferenciaFim
    );

    @Query("""
            select count(distinct e.animal.id)
            from ExameReprodutivo e
            where e.veterinario.id = :veterinarioId
              and (:propriedadeId is null or e.propriedade.id = :propriedadeId)
              and e.animal.categoria in :categorias
              and e.dataHora >= :dataInicio
              and e.dataHora <= :dataFim
            """)
    long countAnimaisComExameRecenteDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("propriedadeId") Long propriedadeId,
            @Param("categorias") Collection<com.reproequinos.vitaequus_api.entities.Enum.Categoria> categorias,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
            select distinct e.animal.id
            from ExameReprodutivo e
            where e.veterinario.id = :veterinarioId
              and (:propriedadeId is null or e.propriedade.id = :propriedadeId)
              and e.animal.categoria in :categorias
              and e.dataHora >= :dataInicio
              and e.dataHora <= :dataFim
            """)
    List<Long> findAnimalIdsComExameRecenteDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("propriedadeId") Long propriedadeId,
            @Param("categorias") Collection<com.reproequinos.vitaequus_api.entities.Enum.Categoria> categorias,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @EntityGraph(attributePaths = {"animal", "propriedade"})
    @Query("""
            select e
            from ExameReprodutivo e
            where e.veterinario.id = :veterinarioId
              and (:propriedadeId is null or e.propriedade.id = :propriedadeId)
              and e.animal.categoria in :categorias
              and e.diametroFolicular is not null
              and e.diametroFolicular >= :diametroMinimo
              and e.dataHora >= :dataInicio
              and e.dataHora <= :dataFim
            order by e.dataHora asc
            """)
    List<ExameReprodutivo> findExamesParaUltrassomDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("propriedadeId") Long propriedadeId,
            @Param("categorias") Collection<com.reproequinos.vitaequus_api.entities.Enum.Categoria> categorias,
            @Param("diametroMinimo") java.math.BigDecimal diametroMinimo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
