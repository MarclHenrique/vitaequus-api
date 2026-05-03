package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import jakarta.validation.constraints.NotNull;

public record StatusAnimalRequestDTO(
        @NotNull StatusAnimal status
) {}