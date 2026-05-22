package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.AtendimentoClinico;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class AtendimentoClinicoSpecifications {

    private AtendimentoClinicoSpecifications() {
    }

    public static Specification<AtendimentoClinico> filtros(
            Long veterinarioId,
            Long animalId,
            TipoAtendimento tipoAtendimento,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    if (animalId != null) {
                        predicates.add(cb.equal(root.get("animal").get("id"), animalId));
                    }
                    if (tipoAtendimento != null) {
                        predicates.add(cb.equal(root.get("tipoAtendimento"), tipoAtendimento));
                    }
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    public static Specification<AtendimentoClinico> timelineAnimal(
            Long animalId,
            Long veterinarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> cb.equal(root.get("animal").get("id"), animalId));
    }

    private static Specification<AtendimentoClinico> periodoDoVeterinario(
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
