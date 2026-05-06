package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.ProprietarioPropriedade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProprietarioPropriedadeRepository extends JpaRepository<ProprietarioPropriedade, Long> {

    boolean existsByProprietarioIdAndPropriedadeId(Long proprietarioId, Long propriedadeId);

    Optional<ProprietarioPropriedade> findByProprietarioIdAndPropriedadeId(Long proprietarioId, Long propriedadeId);

    List<ProprietarioPropriedade> findByPropriedadeId(Long propriedadeId);

    List<ProprietarioPropriedade> findByProprietarioId(Long proprietarioId);

    List<ProprietarioPropriedade> findByPropriedadeIdAndPropriedadeVeterinarioId(Long propriedadeId, Long veterinarioId);

    List<ProprietarioPropriedade> findByProprietarioIdAndPropriedadeVeterinarioId(Long proprietarioId, Long veterinarioId);

    Optional<ProprietarioPropriedade> findByProprietarioIdAndPropriedadeIdAndPropriedadeVeterinarioId(
            Long proprietarioId,
            Long propriedadeId,
            Long veterinarioId
    );

    boolean existsByProprietarioId(Long proprietarioId);

    boolean existsByProprietarioIdAndPropriedade_AtivoTrueAndPropriedadeVeterinarioId(Long proprietarioId, Long veterinarioId);

    @Query("""
            select count(pp) > 0
            from ProprietarioPropriedade pp
            where pp.proprietario.id = :proprietarioId
              and pp.propriedade.id = :propriedadeId
              and pp.propriedade.veterinario.id = :veterinarioId
            """)
    boolean existsByProprietarioAndPropriedadeAndVeterinario(@Param("proprietarioId") Long proprietarioId,
                                                             @Param("propriedadeId") Long propriedadeId,
                                                             @Param("veterinarioId") Long veterinarioId);
}
