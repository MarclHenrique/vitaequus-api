package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.PotroNascido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PotroNascidoRepository extends JpaRepository<PotroNascido, Long> {

    @EntityGraph(attributePaths = {"parto", "animalCriado"})
    List<PotroNascido> findByPartoIdAndPartoVeterinarioId(Long partoId, Long veterinarioId);

    @EntityGraph(attributePaths = {"parto", "animalCriado"})
    Optional<PotroNascido> findByIdAndPartoIdAndPartoVeterinarioId(Long id, Long partoId, Long veterinarioId);

    @EntityGraph(attributePaths = {"parto", "animalCriado"})
    @Query("""
            select p
            from PotroNascido p
            where p.animalCriado.id = :animalId
              and p.parto.veterinario.id = :veterinarioId
              and (:dataInicio is null or p.parto.dataHora >= :dataInicio)
              and (:dataFim is null or p.parto.dataHora <= :dataFim)
            order by p.parto.dataHora desc
            """)
    List<PotroNascido> findTimelineByAnimalCriadoAndVeterinario(
            @Param("animalId") Long animalId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
