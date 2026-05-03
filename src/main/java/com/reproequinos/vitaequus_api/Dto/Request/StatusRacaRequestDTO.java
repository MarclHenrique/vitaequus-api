package com.reproequinos.vitaequus_api.Dto.Request;

import jakarta.validation.constraints.NotNull;

public record StatusRacaRequestDTO(
        @NotNull Integer status // 0 ou 1
) {}