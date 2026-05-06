package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Cuidador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CuidadorRepository extends JpaRepository<Cuidador, Long> {

    boolean existsByNrDocumento(String nrDocumento);

    @Query("""
            select distinct c
            from Cuidador c
            left join c.propriedades cp
            left join cp.propriedade p
            where c.id = :id
              and (c.veterinario.id = :veterinarioId or p.veterinario.id = :veterinarioId)
            """)
    Optional<Cuidador> findByIdAndVeterinarioId(@Param("id") Long id,
                                                @Param("veterinarioId") Long veterinarioId);

    @Query("""
            select distinct c
            from Cuidador c
            left join c.propriedades cp
            left join cp.propriedade p
            where c.veterinario.id = :veterinarioId
               or p.veterinario.id = :veterinarioId
            """)
    java.util.List<Cuidador> findDistinctByVeterinarioId(@Param("veterinarioId") Long veterinarioId);
}
