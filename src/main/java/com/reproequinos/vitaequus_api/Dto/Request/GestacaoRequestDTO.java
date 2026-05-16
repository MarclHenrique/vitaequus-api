package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GestacaoRequestDTO(
        @NotNull
        Long doadoraId,

        @NotNull
        Long coberturaId,

        LocalDate dataDiagnosticoInicial,

        @NotNull
        ResultadoGestacao resultado,

        LocalDate dataPrevisaoParto,
        String observacoes
) {
}
