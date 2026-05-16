package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoProcedimento;
import com.reproequinos.vitaequus_api.entities.Enum.TipoSemen;

import java.time.LocalDateTime;

public record CoberturaResponseDTO(
        Long id,
        Long doadoraId,
        Long doadoraAnimalId,
        String doadoraNome,
        Long produtorId,
        Long produtorAnimalId,
        String produtorNome,
        Long propriedadeId,
        String propriedadeNome,
        Long veterinarioId,
        String veterinarioNome,
        TipoProcedimento tipoProcedimento,
        TipoSemen tipoSemen,
        LocalDateTime dataHora,
        String observacoes
) {
}
