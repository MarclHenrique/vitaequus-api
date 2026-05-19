package com.reproequinos.vitaequus_api.Dto.Response;

import java.time.LocalDateTime;

public record DashboardAgendaItemDTO(
        String tipo,
        String titulo,
        String quando,
        String propriedadeNome,
        String prioridade,
        LocalDateTime dataHora
) {}
