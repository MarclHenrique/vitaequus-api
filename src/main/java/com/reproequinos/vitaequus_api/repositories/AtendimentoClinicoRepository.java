package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.AtendimentoClinico;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AtendimentoClinicoRepository extends JpaRepository<AtendimentoClinico, Long>, JpaSpecificationExecutor<AtendimentoClinico> {

    @EntityGraph(attributePaths = {
            "animal",
            "propriedade",
            "veterinario",
            "medicacoes",
            "medicacoes.insumo"
    })
    Optional<AtendimentoClinico> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @Override
    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario"})
    Page<AtendimentoClinico> findAll(Specification<AtendimentoClinico> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"animal", "propriedade", "veterinario"})
    List<AtendimentoClinico> findAll(Specification<AtendimentoClinico> spec, Sort sort);
}
