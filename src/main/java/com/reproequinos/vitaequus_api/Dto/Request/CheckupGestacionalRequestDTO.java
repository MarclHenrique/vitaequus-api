package com.reproequinos.vitaequus_api.Dto.Request;

import java.time.LocalDateTime;

public record CheckupGestacionalRequestDTO(
        LocalDateTime dataHora,
        String resultado,
        String observacoes
) {
}
