package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoParto;
import com.reproequinos.vitaequus_api.entities.Enum.TipoParto;
import jakarta.validation.constraints.NotNull;

public record PartoUpdateDTO(
        @NotNull
        TipoParto tipoParto,

        @NotNull
        ResultadoParto resultadoParto,

        String intercorrencias,
        String observacoes
) {
}
