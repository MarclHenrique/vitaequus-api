package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.MedicacaoAplicada;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicacaoAplicadaRepository extends JpaRepository<MedicacaoAplicada, Long> {

    @EntityGraph(attributePaths = {"atendimento", "insumo"})
    List<MedicacaoAplicada> findByAtendimentoIdAndAtendimentoVeterinarioId(Long atendimentoId, Long veterinarioId);

    @EntityGraph(attributePaths = {"atendimento", "insumo"})
    Optional<MedicacaoAplicada> findByIdAndAtendimentoIdAndAtendimentoVeterinarioId(
            Long id,
            Long atendimentoId,
            Long veterinarioId
    );
}
