package com.reproequinos.vitaequus_api.Dto.Request;

import jakarta.validation.constraints.NotNull;

public record VincularCuidadorPropriedadeRequestDTO(
        @NotNull Long propriedadeId
) {}