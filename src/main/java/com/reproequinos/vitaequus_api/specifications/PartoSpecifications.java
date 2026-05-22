package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Parto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class PartoSpecifications {

    private PartoSpecifications() {
    }

    public static Specification<Parto> filtros(
            Long veterinarioId,
            Long gestacaoId,
            Long doadoraId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    if (gestacaoId != null) {
                        predicates.add(cb.equal(root.get("gestacao").get("id"), gestacaoId));
                    }
                    if (doadoraId != null) {
                        predicates.add(cb.equal(root.get("doadora").get("id"), doadoraId));
                    }
                    if (propriedadeId != null) {
                        predicates.add(cb.equal(root.get("propriedade").get("id"), propriedadeId));
                    }
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    public static Specification<Parto> timelineDoadora(
            Long animalId,
            Long veterinarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> cb.equal(root.get("doadora").get("animal").get("id"), animalId));
    }

    private static Specification<Parto> periodoDoVeterinario(
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
