package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Proprietario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {
    boolean existsByNrDocumento(String nrDocumento);
    boolean existsByNrDocumentoAndIdNot(String nrDocumento, Long id);
    Optional<Proprietario> findById(Long id);

    @Query("""
            select distinct p
            from Proprietario p
            left join p.proprietarioPropriedades pp
            left join pp.propriedade prop
            where p.veterinario.id = :veterinarioId
               or prop.veterinario.id = :veterinarioId
            """)
    List<Proprietario> findDistinctByVeterinarioId(@Param("veterinarioId") Long veterinarioId);

    @Query(value = """
            select distinct p
            from Proprietario p
            left join p.proprietarioPropriedades pp
            left join pp.propriedade prop
            where (p.veterinario.id = :veterinarioId or prop.veterinario.id = :veterinarioId)
              and (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
              and (:documento is null or lower(p.nrDocumento) like lower(concat('%', :documento, '%')))
              and (:email is null or lower(p.email) like lower(concat('%', :email, '%')))
            """,
            countQuery = """
            select count(distinct p)
            from Proprietario p
            left join p.proprietarioPropriedades pp
            left join pp.propriedade prop
            where (p.veterinario.id = :veterinarioId or prop.veterinario.id = :veterinarioId)
              and (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
              and (:documento is null or lower(p.nrDocumento) like lower(concat('%', :documento, '%')))
              and (:email is null or lower(p.email) like lower(concat('%', :email, '%')))
            """)
    Page<Proprietario> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("nome") String nome,
            @Param("documento") String documento,
            @Param("email") String email,
            Pageable pageable
    );

    @Query("""
            select distinct p
            from Proprietario p
            left join p.proprietarioPropriedades pp
            left join pp.propriedade prop
            where p.id = :id
              and (p.veterinario.id = :veterinarioId or prop.veterinario.id = :veterinarioId)
            """)
    Optional<Proprietario> findByIdAndVeterinarioId(@Param("id") Long id,
                                                    @Param("veterinarioId") Long veterinarioId);
}
