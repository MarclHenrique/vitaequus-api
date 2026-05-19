package com.reproequinos.vitaequus_api.Dto.Response;

import java.util.List;

public record DashboardGeralResponseDTO(
        DashboardResumoCardDTO plantelAtivo,
        DashboardTaxaPrenhezDTO taxaPrenhez,
        DashboardEstoqueDTO alertasEstoque,
        List<DashboardAgendaItemDTO> agendaLembretes,
        DashboardStatusReprodutivoDTO statusReprodutivoAtual
) {}
