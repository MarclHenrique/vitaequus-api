package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Raca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RacaRepository extends JpaRepository<Raca, Long> {

    List<Raca> findByStatus(Integer status); // 0 = ativo

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Raca> findByIdAndStatus(Long id, Integer status);

    @Query("""
            select r
            from Raca r
            where (:nome is null or lower(r.nome) like concat('%', lower(cast(:nome as string)), '%'))
              and (:status is null or r.status = :status)
            """)
    Page<Raca> findByFiltros(
            @Param("nome") String nome,
            @Param("status") Integer status,
            Pageable pageable
    );
}
