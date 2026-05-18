package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao;

import java.time.LocalDate;
import java.util.List;

public record GestacaoResponseDTO(
        Long id,
        Long doadoraId,
        Long doadoraAnimalId,
        String doadoraNome,
        Long coberturaId,
        Long propriedadeId,
        String propriedadeNome,
        Long veterinarioId,
        String veterinarioNome,
        LocalDate dataDiagnosticoInicial,
        ResultadoGestacao resultado,
        StatusGestacao status,
        LocalDate dataPrevisaoParto,
        String observacoes,
        List<CheckupGestacionalResponseDTO> checkups
) {
}
