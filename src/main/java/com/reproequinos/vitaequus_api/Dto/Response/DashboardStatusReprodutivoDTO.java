package com.reproequinos.vitaequus_api.Dto.Response;

public record DashboardStatusReprodutivoDTO(
        DashboardStatusItemDTO eguasVazias,
        DashboardStatusItemDTO emAcompanhamento,
        DashboardStatusItemDTO prenhezConfirmada,
        long totalMonitorado,
        String descricaoTotal
) {}
