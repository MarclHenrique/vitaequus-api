package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import jakarta.validation.constraints.NotNull;

public record GestacaoResultadoUpdateDTO(
        @NotNull
        ResultadoGestacao resultado,

        String observacoes
) {
}
