package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Cuidador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
            select distinct c
            from Cuidador c
            left join c.propriedades cp
            left join cp.propriedade p
            where (c.veterinario.id = :veterinarioId or p.veterinario.id = :veterinarioId)
              and (:nome is null or lower(c.nome) like lower(concat('%', :nome, '%')))
              and (:telefone is null or lower(c.telefone) like lower(concat('%', :telefone, '%')))
              and (
                    :ativo is null
                    or (:ativo = true and (c.veterinario.id = :veterinarioId or cp.status = 0))
                    or (:ativo = false and cp.status = 1)
                  )
            """,
            countQuery = """
            select count(distinct c)
            from Cuidador c
            left join c.propriedades cp
            left join cp.propriedade p
            where (c.veterinario.id = :veterinarioId or p.veterinario.id = :veterinarioId)
              and (:nome is null or lower(c.nome) like lower(concat('%', :nome, '%')))
              and (:telefone is null or lower(c.telefone) like lower(concat('%', :telefone, '%')))
              and (
                    :ativo is null
                    or (:ativo = true and (c.veterinario.id = :veterinarioId or cp.status = 0))
                    or (:ativo = false and cp.status = 1)
                  )
            """)
    Page<Cuidador> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("nome") String nome,
            @Param("telefone") String telefone,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );
}
