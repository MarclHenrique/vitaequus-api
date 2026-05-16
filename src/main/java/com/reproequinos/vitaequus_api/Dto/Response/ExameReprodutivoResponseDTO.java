package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.CorpoLuteo;
import com.reproequinos.vitaequus_api.entities.Enum.EdemaUterino;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExameReprodutivoResponseDTO(
        Long id,
        Long animalId,
        String animalNome,
        Long propriedadeId,
        String propriedadeNome,
        Long veterinarioId,
        String veterinarioNome,
        LocalDateTime dataHora,
        BigDecimal diametroFolicular,
        EdemaUterino edemaUterino,
        CorpoLuteo corpoLuteo,
        Long insumoId,
        String insumoNomeComercial,
        String observacoes
) {
}
