package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;

import java.time.LocalDateTime;
import java.util.List;

public record AtendimentoClinicoResponseDTO(
        Long id,
        Long animalId,
        String animalNome,
        Long propriedadeId,
        String propriedadeNome,
        Long veterinarioId,
        String veterinarioNome,
        LocalDateTime dataHora,
        TipoAtendimento tipoAtendimento,
        String queixaPrincipal,
        String diagnosticoPresuntivo,
        String conduta,
        List<MedicacaoAplicadaResponseDTO> medicacoes
) {
}
