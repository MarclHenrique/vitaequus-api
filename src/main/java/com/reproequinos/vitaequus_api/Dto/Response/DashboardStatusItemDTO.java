package com.reproequinos.vitaequus_api.Dto.Response;

public record DashboardStatusItemDTO(
        long total,
        int percentual,
        String descricao
) {}
