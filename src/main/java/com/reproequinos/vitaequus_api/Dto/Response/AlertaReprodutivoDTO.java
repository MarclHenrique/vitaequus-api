package com.reproequinos.vitaequus_api.Dto.Response;

public record AlertaReprodutivoDTO(
        String tipo,
        String titulo,
        Long animalId,
        String animalNome,
        String descricao,
        String prioridade
) {}
