package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.CheckupGestacional;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public final class CheckupGestacionalSpecifications {

    private CheckupGestacionalSpecifications() {
    }

    public static Specification<CheckupGestacional> filtrosDaGestacao(
            Long gestacaoId,
            Long veterinarioId,
            String resultado,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return filtrosComuns(resultado, dataInicio, dataFim)
                .and((root, query, cb) -> cb.and(
                        cb.equal(root.get("gestacao").get("id"), gestacaoId),
                        cb.equal(root.get("gestacao").get("cobertura").get("veterinario").get("id"), veterinarioId)
                ));
    }

    public static Specification<CheckupGestacional> filtrosGlobais(
            Long veterinarioId,
            Long gestacaoId,
            String resultado,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return filtrosComuns(resultado, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    predicates.add(cb.or(
                            cb.equal(root.get("gestacao").get("cobertura").get("veterinario").get("id"), veterinarioId),
                            cb.equal(root.get("veterinario").get("id"), veterinarioId)
                    ));
                    if (gestacaoId != null) {
                        predicates.add(cb.equal(root.get("gestacao").get("id"), gestacaoId));
                    }
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    public static Specification<CheckupGestacional> timelineDoadora(
            Long animalId,
            Long veterinarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return filtrosComuns(null, dataInicio, dataFim)
                .and((root, query, cb) -> cb.and(
                        cb.equal(root.get("gestacao").get("doadora").get("animal").get("id"), animalId),
                        cb.equal(root.get("gestacao").get("cobertura").get("veterinario").get("id"), veterinarioId)
                ));
    }

    public static Specification<CheckupGestacional> proximosDashboard(
            Long veterinarioId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return filtrosComuns(null, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    predicates.add(cb.equal(root.get("gestacao").get("cobertura").get("veterinario").get("id"), veterinarioId));
                    if (propriedadeId != null) {
                        predicates.add(cb.equal(root.get("gestacao").get("cobertura").get("propriedade").get("id"), propriedadeId));
                    }
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    private static Specification<CheckupGestacional> filtrosComuns(
            String resultado,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (StringUtils.hasText(resultado)) {
                predicates.add(cb.like(cb.lower(root.get("resultado")), "%" + resultado.toLowerCase(Locale.ROOT) + "%"));
            }
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
