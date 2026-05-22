package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.PotroNascido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PotroNascidoRepository extends JpaRepository<PotroNascido, Long>, JpaSpecificationExecutor<PotroNascido> {

    @EntityGraph(attributePaths = {"parto", "animalCriado"})
    List<PotroNascido> findByPartoIdAndPartoVeterinarioId(Long partoId, Long veterinarioId);

    @EntityGraph(attributePaths = {"parto", "animalCriado"})
    Optional<PotroNascido> findByIdAndPartoIdAndPartoVeterinarioId(Long id, Long partoId, Long veterinarioId);

    @Override
    @EntityGraph(attributePaths = {"parto", "animalCriado"})
    List<PotroNascido> findAll(Specification<PotroNascido> spec, Sort sort);
}
