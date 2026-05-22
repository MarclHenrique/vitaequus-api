package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Raca;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

public final class RacaSpecifications {

    private RacaSpecifications() {
    }

    public static Specification<Raca> filtros(String nome, Integer status) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (StringUtils.hasText(nome)) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase(Locale.ROOT) + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
