package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Proprietario;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

public final class ProprietarioSpecifications {

    private ProprietarioSpecifications() {
    }

    public static Specification<Proprietario> filtros(
            Long veterinarioId,
            String nome,
            String documento,
            String email
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            var vinculo = root.join("proprietarioPropriedades", JoinType.LEFT);
            var propriedade = vinculo.join("propriedade", JoinType.LEFT);
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.or(
                    cb.equal(root.get("veterinario").get("id"), veterinarioId),
                    cb.equal(propriedade.get("veterinario").get("id"), veterinarioId)
            ));
            if (StringUtils.hasText(nome)) {
                predicates.add(cb.like(cb.lower(root.get("nome")), contains(nome)));
            }
            if (StringUtils.hasText(documento)) {
                predicates.add(cb.like(cb.lower(root.get("nrDocumento")), contains(documento)));
            }
            if (StringUtils.hasText(email)) {
                predicates.add(cb.like(cb.lower(root.get("email")), contains(email)));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String contains(String value) {
        return "%" + value.toLowerCase(Locale.ROOT) + "%";
    }
}
