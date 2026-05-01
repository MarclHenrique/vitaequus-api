package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.ProprietarioPropriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProprietarioPropriedadeRepository extends JpaRepository<ProprietarioPropriedade, Long> {

    boolean existsByProprietarioIdAndPropriedadeId(Long proprietarioId, Long propriedadeId);

    Optional<ProprietarioPropriedade> findByProprietarioIdAndPropriedadeId(Long proprietarioId, Long propriedadeId);

    List<ProprietarioPropriedade> findByPropriedadeId(Long propriedadeId);

    List<ProprietarioPropriedade> findByProprietarioId(Long proprietarioId);

    boolean existsByProprietarioId(Long proprietarioId);

    boolean existsByProprietarioIdAndPropriedade_AtivoTrue(Long proprietarioId);
}