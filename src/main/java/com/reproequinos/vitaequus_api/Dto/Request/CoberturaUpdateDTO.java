package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoProcedimento;
import com.reproequinos.vitaequus_api.entities.Enum.TipoSemen;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CoberturaUpdateDTO(
        @NotNull
        TipoProcedimento tipoProcedimento,

        TipoSemen tipoSemen,
        LocalDateTime dataHora,
        String observacoes
) {
}
