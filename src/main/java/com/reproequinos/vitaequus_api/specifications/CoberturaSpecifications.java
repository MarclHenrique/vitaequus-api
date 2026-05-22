package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Cobertura;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class CoberturaSpecifications {

    private CoberturaSpecifications() {
    }

    public static Specification<Cobertura> filtros(
            Long veterinarioId,
            Long doadoraAnimalId,
            Long produtorAnimalId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    if (doadoraAnimalId != null) {
                        predicates.add(cb.equal(root.get("doadora").get("animal").get("id"), doadoraAnimalId));
                    }
                    if (produtorAnimalId != null) {
                        predicates.add(cb.equal(root.get("produtor").get("animal").get("id"), produtorAnimalId));
                    }
                    if (propriedadeId != null) {
                        predicates.add(cb.equal(root.get("propriedade").get("id"), propriedadeId));
                    }
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    public static Specification<Cobertura> timelineAnimal(
            Long animalId,
            Long veterinarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> cb.or(
                        cb.equal(root.get("doadora").get("animal").get("id"), animalId),
                        cb.equal(root.get("produtor").get("animal").get("id"), animalId)
                ));
    }

    public static Specification<Cobertura> semGestacaoNoPeriodo(
            Long veterinarioId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return periodoDoVeterinario(veterinarioId, dataInicio, dataFim)
                .and((root, query, cb) -> {
                    var predicates = new ArrayList<Predicate>();
                    if (propriedadeId != null) {
                        predicates.add(cb.equal(root.get("propriedade").get("id"), propriedadeId));
                    }
                    var subquery = query.subquery(Long.class);
                    var gestacao = subquery.from(Gestacao.class);
                    subquery.select(cb.literal(1L));
                    subquery.where(cb.equal(gestacao.get("cobertura").get("id"), root.get("id")));
                    predicates.add(cb.not(cb.exists(subquery)));
                    return cb.and(predicates.toArray(Predicate[]::new));
                });
    }

    private static Specification<Cobertura> periodoDoVeterinario(
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
