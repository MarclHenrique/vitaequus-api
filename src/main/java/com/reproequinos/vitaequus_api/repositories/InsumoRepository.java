package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Insumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    @EntityGraph(attributePaths = "fornecedor")
    Page<Insumo> findByVeterinarioId(Long veterinarioId, Pageable pageable);

    @EntityGraph(attributePaths = "fornecedor")
    Optional<Insumo> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    @EntityGraph(attributePaths = "fornecedor")
    Page<Insumo> findByFornecedorIdAndVeterinarioId(Long fornecedorId, Long veterinarioId, Pageable pageable);

    boolean existsByFornecedorIdAndVeterinarioId(Long fornecedorId, Long veterinarioId);

    @EntityGraph(attributePaths = "fornecedor")
    @Query("""
            select i
            from Insumo i
            where i.veterinario.id = :veterinarioId
              and (:tipo is null or i.tipo = :tipo)
              and (:fornecedorId is null or i.fornecedor.id = :fornecedorId)
            """)
    Page<Insumo> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("tipo") TipoInsumo tipo,
            @Param("fornecedorId") Long fornecedorId,
            Pageable pageable
    );
}
