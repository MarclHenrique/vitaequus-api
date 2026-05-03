package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.CuidadorPropriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuidadorPropriedadeRepository extends JpaRepository<CuidadorPropriedade, Long> {

    boolean existsByCuidadorIdAndPropriedadeId(Long cuidadorId, Long propriedadeId);

    Optional<CuidadorPropriedade> findByCuidadorIdAndPropriedadeId(Long cuidadorId, Long propriedadeId);

    List<CuidadorPropriedade> findByCuidadorId(Long cuidadorId);

    List<CuidadorPropriedade> findByPropriedadeId(Long propriedadeId);

    List<CuidadorPropriedade> findByPropriedadeVeterinarioId(Long veterinarioId);

    Optional<CuidadorPropriedade> findByCuidadorIdAndPropriedadeIdAndPropriedadeVeterinarioId(
            Long cuidadorId,
            Long propriedadeId,
            Long veterinarioId
    );

    boolean existsByCuidadorIdAndPropriedadeIdAndPropriedadeVeterinarioId(
            Long cuidadorId,
            Long propriedadeId,
            Long veterinarioId
    );
}