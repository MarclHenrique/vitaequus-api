package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.CorpoLuteo;
import com.reproequinos.vitaequus_api.entities.Enum.EdemaUterino;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record ExameReprodutivoUpdateDTO(
        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal diametroFolicular,

        EdemaUterino edemaUterino,
        CorpoLuteo corpoLuteo,
        Long insumoId,
        String observacoes
) {
}
