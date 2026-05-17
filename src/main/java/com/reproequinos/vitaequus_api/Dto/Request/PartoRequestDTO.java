package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoParto;
import com.reproequinos.vitaequus_api.entities.Enum.TipoParto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PartoRequestDTO(
        @NotNull
        Long gestacaoId,

        @NotNull
        Long propriedadeId,

        LocalDateTime dataHora,

        @NotNull
        TipoParto tipoParto,

        @NotNull
        ResultadoParto resultadoParto,

        String intercorrencias,
        String observacoes,

        @Valid
        List<PotroNascidoRequestDTO> potros
) {
}
