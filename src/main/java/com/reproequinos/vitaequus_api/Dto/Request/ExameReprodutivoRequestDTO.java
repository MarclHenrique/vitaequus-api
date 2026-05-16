package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.CorpoLuteo;
import com.reproequinos.vitaequus_api.entities.Enum.EdemaUterino;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExameReprodutivoRequestDTO(
        @NotNull
        Long animalId,

        @NotNull
        Long propriedadeId,

        LocalDateTime dataHora,

        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal diametroFolicular,

        EdemaUterino edemaUterino,
        CorpoLuteo corpoLuteo,
        Long insumoId,
        String observacoes
) {
}
