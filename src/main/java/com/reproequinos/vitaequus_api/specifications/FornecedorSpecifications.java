package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Fornecedor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

public final class FornecedorSpecifications {

    private FornecedorSpecifications() {
    }

    public static Specification<Fornecedor> filtros(Long veterinarioId, String nome) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("veterinario").get("id"), veterinarioId));
            if (StringUtils.hasText(nome)) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase(Locale.ROOT) + "%"));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
