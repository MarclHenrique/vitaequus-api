package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GestacaoRepository extends JpaRepository<Gestacao, Long> {

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "cobertura", "cobertura.propriedade", "cobertura.veterinario"
    })
    Optional<Gestacao> findByIdAndCoberturaVeterinarioId(Long id, Long veterinarioId);

    boolean existsByCoberturaIdAndCoberturaVeterinarioId(Long coberturaId, Long veterinarioId);

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "cobertura", "cobertura.propriedade", "cobertura.veterinario"
    })
    @Query("""
            select g
            from Gestacao g
            where g.cobertura.veterinario.id = :veterinarioId
              and (:doadoraId is null or g.doadora.id = :doadoraId)
              and (:coberturaId is null or g.cobertura.id = :coberturaId)
              and (:resultado is null or g.resultado = :resultado)
              and (:status is null or g.status = :status)
              and (:dataInicio is null or g.dataDiagnosticoInicial >= :dataInicio)
              and (:dataFim is null or g.dataDiagnosticoInicial <= :dataFim)
            """)
    Page<Gestacao> findByFiltros(
            @Param("veterinarioId") Long veterinarioId,
            @Param("doadoraId") Long doadoraId,
            @Param("coberturaId") Long coberturaId,
            @Param("resultado") ResultadoGestacao resultado,
            @Param("status") StatusGestacao status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal",
            "cobertura", "cobertura.propriedade", "cobertura.veterinario"
    })
    @Query("""
            select g
            from Gestacao g
            where g.doadora.animal.id = :animalId
              and g.cobertura.veterinario.id = :veterinarioId
              and (:dataInicio is null or g.dataDiagnosticoInicial >= :dataInicioDate)
              and (:dataFim is null or g.dataDiagnosticoInicial <= :dataFimDate)
            order by g.dataDiagnosticoInicial desc
            """)
    List<Gestacao> findTimelineByDoadoraAnimalAndVeterinario(
            @Param("animalId") Long animalId,
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("dataInicioDate") LocalDate dataInicioDate,
            @Param("dataFimDate") LocalDate dataFimDate
    );

    @EntityGraph(attributePaths = {
            "doadora", "doadora.animal", "doadora.animal.propriedade",
            "cobertura", "cobertura.propriedade", "cobertura.veterinario"
    })
    @Query("""
            select g
            from Gestacao g
            where g.cobertura.veterinario.id = :veterinarioId
              and g.resultado = com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao.PRENHE
              and g.status = com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao.EM_ANDAMENTO
              and (:propriedadeId is null or g.cobertura.propriedade.id = :propriedadeId)
            order by g.dataDiagnosticoInicial desc
            """)
    List<Gestacao> findPrenhesEmAndamentoDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("propriedadeId") Long propriedadeId
    );

    @Query("""
            select count(g)
            from Gestacao g
            where g.cobertura.veterinario.id = :veterinarioId
              and g.resultado = com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao.PRENHE
              and g.status = com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao.EM_ANDAMENTO
              and (:propriedadeId is null or g.cobertura.propriedade.id = :propriedadeId)
            """)
    long countPrenhesEmAndamentoDashboard(
            @Param("veterinarioId") Long veterinarioId,
            @Param("propriedadeId") Long propriedadeId
    );
}
