package com.reproequinos.vitaequus_api.specifications;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;

public final class GestacaoSpecifications {

    private GestacaoSpecifications() {
    }

    public static Specification<Gestacao> filtros(
            Long veterinarioId,
            Long doadoraId,
            Long coberturaId,
            ResultadoGestacao resultado,
            StatusGestacao status,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("cobertura").get("veterinario").get("id"), veterinarioId));
            if (doadoraId != null) {
                predicates.add(cb.equal(root.get("doadora").get("id"), doadoraId));
            }
            if (coberturaId != null) {
                predicates.add(cb.equal(root.get("cobertura").get("id"), coberturaId));
            }
            if (resultado != null) {
                predicates.add(cb.equal(root.get("resultado"), resultado));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataDiagnosticoInicial"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataDiagnosticoInicial"), dataFim));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Gestacao> timelineDoadora(
            Long animalId,
            Long veterinarioId,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("doadora").get("animal").get("id"), animalId));
            predicates.add(cb.equal(root.get("cobertura").get("veterinario").get("id"), veterinarioId));
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataDiagnosticoInicial"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataDiagnosticoInicial"), dataFim));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Gestacao> prenhesEmAndamento(Long veterinarioId, Long propriedadeId) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("cobertura").get("veterinario").get("id"), veterinarioId));
            predicates.add(cb.equal(root.get("resultado"), ResultadoGestacao.PRENHE));
            predicates.add(cb.equal(root.get("status"), StatusGestacao.EM_ANDAMENTO));
            if (propriedadeId != null) {
                predicates.add(cb.equal(root.get("cobertura").get("propriedade").get("id"), propriedadeId));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
