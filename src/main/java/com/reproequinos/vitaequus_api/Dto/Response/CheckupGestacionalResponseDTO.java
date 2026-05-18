package com.reproequinos.vitaequus_api.Dto.Response;

import java.time.LocalDateTime;

public record CheckupGestacionalResponseDTO(
        Long id,
        Long gestacaoId,
        Long veterinarioId,
        String veterinarioNome,
        LocalDateTime dataHora,
        String resultado,
        String observacoes,
        String doadoraNome,
        String propriedadeNome
) {
}
