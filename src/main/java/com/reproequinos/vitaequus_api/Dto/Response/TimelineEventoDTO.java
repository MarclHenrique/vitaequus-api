package com.reproequinos.vitaequus_api.Dto.Response;

import java.time.LocalDateTime;

public record TimelineEventoDTO(
        Long id,
        String tipo,
        String titulo,
        String descricao,
        LocalDateTime dataHora
) {}