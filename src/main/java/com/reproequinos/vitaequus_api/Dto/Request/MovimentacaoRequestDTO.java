package com.reproequinos.vitaequus_api.Dto.Request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MovimentacaoRequestDTO(

        @NotNull
        Long propriedadeId,

        LocalDateTime dataMovimentacao,

        String motivo
) {}