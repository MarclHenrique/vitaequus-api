package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoParto;
import com.reproequinos.vitaequus_api.entities.Enum.TipoParto;

import java.time.LocalDateTime;
import java.util.List;

public record PartoResponseDTO(
        Long id,
        Long gestacaoId,
        Long doadoraId,
        Long doadoraAnimalId,
        String doadoraNome,
        Long propriedadeId,
        String propriedadeNome,
        Long veterinarioId,
        String veterinarioNome,
        LocalDateTime dataHora,
        TipoParto tipoParto,
        ResultadoParto resultadoParto,
        String intercorrencias,
        String observacoes,
        List<PotroNascidoResponseDTO> potros
) {
}
