package com.reproequinos.vitaequus_api.Dto.Response;

import java.util.List;

public record DashboardReprodutivoResponseDTO(
        DashboardCardsDTO cards,
        StatusReprodutivoAtualDTO statusReprodutivoAtual,
        List<AlertaReprodutivoDTO> proximasAcoesAlertas
) {}
