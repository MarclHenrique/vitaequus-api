package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.MedicacaoAplicada;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicacaoAplicadaRepository extends JpaRepository<MedicacaoAplicada, Long> {

    @EntityGraph(attributePaths = "insumo")
    List<MedicacaoAplicada> findByAtendimentoId(Long atendimentoId);

    @EntityGraph(attributePaths = {"insumo", "atendimento"})
    Optional<MedicacaoAplicada> findByIdAndAtendimentoVeterinarioId(Long id, Long veterinarioId);
}
