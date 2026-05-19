package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Insumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
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
              and (:estoqueBaixo is null or :estoqueBaixo = false or (
                    i.estoqueAtual is not null
                    and i.estoqueMinimo is not null
                    and i.estoqueAtual <= i.estoqueMinimo
              ))
              and (:vencendoAte is null or (
                    i.dataValidade is not null
                    and i.dataValidade <= :vencendoAte
              ))
            """)
    Page<Insumo> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("tipo") TipoInsumo tipo,
            @Param("fornecedorId") Long fornecedorId,
            @Param("estoqueBaixo") Boolean estoqueBaixo,
            @Param("vencendoAte") LocalDate vencendoAte,
            Pageable pageable
    );

    @Query("""
            select count(i)
            from Insumo i
            where i.veterinario.id = :veterinarioId
              and (
                    (
                        i.estoqueAtual is not null
                        and i.estoqueMinimo is not null
                        and i.estoqueAtual <= i.estoqueMinimo
                    )
                    or (
                        i.dataValidade is not null
                        and i.dataValidade <= :dataValidadeLimite
                    )
              )
            """)
    long countAlertasEstoqueDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataValidadeLimite") LocalDate dataValidadeLimite
    );

    @Query("""
            select i
            from Insumo i
            where i.veterinario.id = :veterinarioId
              and (
                    (
                        i.estoqueAtual is not null
                        and i.estoqueMinimo is not null
                        and i.estoqueAtual <= i.estoqueMinimo
                    )
                    or (
                        i.dataValidade is not null
                        and i.dataValidade <= :dataValidadeLimite
                    )
              )
            order by i.dataValidade asc nulls last, i.nomeComercial asc
            """)
    List<Insumo> findAlertasEstoqueDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataValidadeLimite") LocalDate dataValidadeLimite
    );
}
