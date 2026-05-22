package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.PotroNascido;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class PotroNascidoSpecifications {

    private PotroNascidoSpecifications() {
    }

    public static Specification<PotroNascido> timelineAnimalCriado(
            Long animalId,
            Long veterinarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("animalCriado").get("id"), animalId));
            predicates.add(cb.equal(root.get("parto").get("veterinario").get("id"), veterinarioId));
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("parto").get("dataHora"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("parto").get("dataHora"), dataFim));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
