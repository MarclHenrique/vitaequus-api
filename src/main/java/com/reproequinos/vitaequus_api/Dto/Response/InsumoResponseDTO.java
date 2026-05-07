package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;

public record InsumoResponseDTO(
        Long id,
        String nomeComercial,
        TipoInsumo tipo,
        String principioAtivo,
        Long fornecedorId,
        String fornecedorNome
) {
}
