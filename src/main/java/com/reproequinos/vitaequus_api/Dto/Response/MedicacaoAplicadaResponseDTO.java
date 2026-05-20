package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Enum.ViaAdministracao;

import java.math.BigDecimal;

public record MedicacaoAplicadaResponseDTO(
        Long id,
        Long atendimentoId,
        Long insumoId,
        String insumoNomeComercial,
        String insumoNome,
        TipoInsumo tipoInsumo,
        String dose,
        BigDecimal quantidadeAplicada,
        ViaAdministracao viaAdministracao,
        String observacoes
) {
}
