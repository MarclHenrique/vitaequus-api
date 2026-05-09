package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Enum.ViaAdministracao;

public record MedicacaoAplicadaResponseDTO(
        Long id,
        Long atendimentoId,
        Long insumoId,
        String insumoNomeComercial,
        TipoInsumo tipoInsumo,
        String dose,
        ViaAdministracao viaAdministracao,
        String observacoes
) {
}
