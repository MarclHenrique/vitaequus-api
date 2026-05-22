package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.ExameReprodutivo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public final class ExameReprodutivoSpecifications {

    private ExameReprodutivoSpecifications() {
    }

    public static Specification<ExameReprodutivo> filtros(
            Long veterinarioId,
            Long animalId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    if (animalId != null) {
                        predicates.add(cb.equal(root.get("animal").get("id"), animalId));
                    }
                    if (propriedadeId != null) {
                        predicates.add(cb.equal(root.get("propriedade").get("id"), propriedadeId));
                    }
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    public static Specification<ExameReprodutivo> timelineAnimal(
            Long animalId,
            Long veterinarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> cb.equal(root.get("animal").get("id"), animalId));
    }

    public static Specification<ExameReprodutivo> dashboardPeriodo(
            Long veterinarioId,
            Long propriedadeId,
            Collection<Categoria> categorias,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    predicates.add(root.get("animal").get("categoria").in(categorias));
                    if (propriedadeId != null) {
                        predicates.add(cb.equal(root.get("propriedade").get("id"), propriedadeId));
                    }
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    public static Specification<ExameReprodutivo> dashboardUltrassom(
            Long veterinarioId,
            Long propriedadeId,
            Collection<Categoria> categorias,
            BigDecimal diametroMinimo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return dashboardPeriodo(veterinarioId, propriedadeId, categorias, dataInicio, dataFim)
                .and((root, query, cb) -> cb.and(
                        cb.isNotNull(root.get("diametroFolicular")),
                        cb.greaterThanOrEqualTo(root.get("diametroFolicular"), diametroMinimo)
                ));
    }

    private static Specification<ExameReprodutivo> periodoDoVeterinario(
            Long veterinarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("veterinario").get("id"), veterinarioId));
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataHora"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataHora"), dataFim));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
