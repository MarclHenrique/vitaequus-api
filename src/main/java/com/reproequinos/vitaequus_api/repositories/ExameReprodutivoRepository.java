package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.ExameReprodutivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExameReprodutivoRepository extends JpaRepository<ExameReprodutivo, Long>, JpaSpecificationExecutor<ExameReprodutivo> {

    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario", "insumo"})
    Optional<ExameReprodutivo> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @Override
    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario", "insumo"})
    Page<ExameReprodutivo> findAll(Specification<ExameReprodutivo> spec, Pageable pageable);

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

}
