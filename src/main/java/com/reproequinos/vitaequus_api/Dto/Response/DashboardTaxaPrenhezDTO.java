package com.reproequinos.vitaequus_api.Dto.Response;

public record DashboardTaxaPrenhezDTO(
        int percentual,
        long prenhes,
        long totalMatrizes,
        String descricao
) {}
