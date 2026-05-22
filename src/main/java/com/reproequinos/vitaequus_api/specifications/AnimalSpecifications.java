package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class AnimalSpecifications {

    private AnimalSpecifications() {
    }

    public static Specification<Animal> dashboardAtivos(Long veterinarioId, Long propriedadeId) {
        return (root, query, cb) -> {
            var predicate = cb.and(
                    cb.equal(root.get("propriedade").get("veterinario").get("id"), veterinarioId),
                    cb.equal(root.get("status"), StatusAnimal.ATIVO)
            );
            return propriedadeId == null
                    ? predicate
                    : cb.and(predicate, cb.equal(root.get("propriedade").get("id"), propriedadeId));
        };
    }

    public static Specification<Animal> dashboardMatrizes(
            Long veterinarioId,
            Collection<Categoria> categorias,
            Long propriedadeId
    ) {
        return (root, query, cb) -> {
            var predicate = cb.and(
                    cb.equal(root.get("propriedade").get("veterinario").get("id"), veterinarioId),
                    root.get("categoria").in(categorias)
            );
            return propriedadeId == null
                    ? predicate
                    : cb.and(predicate, cb.equal(root.get("propriedade").get("id"), propriedadeId));
        };
    }

    public static Specification<Animal> dashboardReprodutivos(
            Long veterinarioId,
            Collection<Categoria> categorias,
            Long propriedadeId
    ) {
        return dashboardMatrizes(veterinarioId, categorias, propriedadeId)
                .and((root, query, cb) -> cb.equal(root.get("status"), StatusAnimal.ATIVO));
    }
}
