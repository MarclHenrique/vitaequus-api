package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

public final class PropriedadeSpecifications {

    private PropriedadeSpecifications() {
    }

    public static Specification<Propriedade> filtros(
            Long veterinarioId,
            String nome,
            String cidade,
            String estado,
            TipoPropriedade tipoPropriedade,
            Boolean ativo
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("veterinario").get("id"), veterinarioId));

            if (StringUtils.hasText(nome)) {
                predicates.add(cb.like(cb.lower(root.get("nome")), contains(nome)));
            }
            if (StringUtils.hasText(cidade)) {
                predicates.add(cb.like(cb.lower(root.get("cidade")), contains(cidade)));
            }
            if (StringUtils.hasText(estado)) {
                predicates.add(cb.equal(cb.lower(root.get("estado")), estado.toLowerCase(Locale.ROOT)));
            }
            if (tipoPropriedade != null) {
                predicates.add(cb.equal(root.get("tipoPropriedade"), tipoPropriedade));
            }
            if (ativo != null) {
                predicates.add(cb.equal(root.get("ativo"), ativo));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String contains(String value) {
        return "%" + value.toLowerCase(Locale.ROOT) + "%";
    }
}
