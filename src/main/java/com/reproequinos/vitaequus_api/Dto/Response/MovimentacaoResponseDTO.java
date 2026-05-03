package com.reproequinos.vitaequus_api.Dto.Response;

import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(
        Long id,
        Long animalId,
        String nomeAnimal,
        Long propriedadeId,
        String nomePropriedade,
        LocalDateTime dataMovimentacao,
        String motivo
) {}