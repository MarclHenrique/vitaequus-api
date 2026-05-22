package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
    List<Propriedade> findByAtivoTrueAndVeterinarioId(Long veterinarioId);

    @Query("""
            select p
            from Propriedade p
            where p.veterinario.id = :veterinarioId
              and (:nome is null or lower(p.nome) like concat('%', lower(cast(:nome as string)), '%'))
              and (:cidade is null or lower(p.cidade) like concat('%', lower(cast(:cidade as string)), '%'))
              and (:estado is null or lower(p.estado) = lower(cast(:estado as string)))
              and (:tipoPropriedade is null or p.tipoPropriedade = :tipoPropriedade)
              and (:ativo is null or p.ativo = :ativo)
            """)
    Page<Propriedade> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("nome") String nome,
            @Param("cidade") String cidade,
            @Param("estado") String estado,
            @Param("tipoPropriedade") TipoPropriedade tipoPropriedade,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );

    Optional<Propriedade> findByIdAndAtivoTrueAndVeterinarioId(Long id, Long veterinarioId);
}
