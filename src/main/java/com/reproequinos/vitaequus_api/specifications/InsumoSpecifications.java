package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Insumo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;

public final class InsumoSpecifications {

    private InsumoSpecifications() {
    }

    public static Specification<Insumo> filtros(
            Long veterinarioId,
            TipoInsumo tipo,
            Long fornecedorId,
            Boolean estoqueBaixo,
            LocalDate vencendoAte
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("veterinario").get("id"), veterinarioId));
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }
            if (fornecedorId != null) {
                predicates.add(cb.equal(root.get("fornecedor").get("id"), fornecedorId));
            }
            if (Boolean.TRUE.equals(estoqueBaixo)) {
                predicates.add(cb.and(
                        cb.isNotNull(root.get("estoqueAtual")),
                        cb.isNotNull(root.get("estoqueMinimo")),
                        cb.lessThanOrEqualTo(root.get("estoqueAtual"), root.get("estoqueMinimo"))
                ));
            }
            if (vencendoAte != null) {
                predicates.add(cb.and(
                        cb.isNotNull(root.get("dataValidade")),
                        cb.lessThanOrEqualTo(root.get("dataValidade"), vencendoAte)
                ));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
