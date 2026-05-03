package com.reproequinos.vitaequus_api.Dto.Response;

public record CuidadorPropriedadeResponseDTO(
        Long id,
        Long cuidadorId,
        String nomeCuidador,
        Long propriedadeId,
        String nomePropriedade,
        Integer status
) {}