package com.reproequinos.vitaequus_api.Dto.Response;

public record AnimalStatusReprodutivoDTO(
        Long animalId,
        String nome,
        String iniciais,
        Long propriedadeId,
        String propriedadeNome,
        String box,
        String descricao
) {}
