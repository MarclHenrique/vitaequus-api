package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Cuidador;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

public final class CuidadorSpecifications {

    private CuidadorSpecifications() {
    }

    public static Specification<Cuidador> filtros(
            Long veterinarioId,
            String nome,
            String telefone,
            Boolean ativo
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            var vinculo = root.join("propriedades", JoinType.LEFT);
            var propriedade = vinculo.join("propriedade", JoinType.LEFT);
            var raizDoVeterinario = cb.equal(root.get("veterinario").get("id"), veterinarioId);
            var propriedadeDoVeterinario = cb.equal(propriedade.get("veterinario").get("id"), veterinarioId);
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.or(raizDoVeterinario, propriedadeDoVeterinario));

            if (StringUtils.hasText(nome)) {
                predicates.add(cb.like(cb.lower(root.get("nome")), contains(nome)));
            }
            if (StringUtils.hasText(telefone)) {
                predicates.add(cb.like(cb.lower(root.get("telefone")), contains(telefone)));
            }
            if (Boolean.TRUE.equals(ativo)) {
                predicates.add(cb.or(raizDoVeterinario, cb.equal(vinculo.get("status"), 0)));
            }
            if (Boolean.FALSE.equals(ativo)) {
                predicates.add(cb.equal(vinculo.get("status"), 1));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String contains(String value) {
        return "%" + value.toLowerCase(Locale.ROOT) + "%";
    }
}
