package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    List<Fornecedor> findByVeterinarioId(Long veterinarioId);

    @Query("""
            select f
            from Fornecedor f
            where f.veterinario.id = :veterinarioId
              and (:nome is null or lower(f.nome) like lower(concat('%', :nome, '%')))
            """)
    Page<Fornecedor> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("nome") String nome,
            Pageable pageable
    );

    Optional<Fornecedor> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    boolean existsByIdAndVeterinarioId(Long id, Long veterinarioId);
}
