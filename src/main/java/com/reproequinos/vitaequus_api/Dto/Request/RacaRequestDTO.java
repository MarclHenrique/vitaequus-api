package com.reproequinos.vitaequus_api.Dto.Request;

import jakarta.validation.constraints.NotBlank;

public record RacaRequestDTO(
        @NotBlank String nome
) {}