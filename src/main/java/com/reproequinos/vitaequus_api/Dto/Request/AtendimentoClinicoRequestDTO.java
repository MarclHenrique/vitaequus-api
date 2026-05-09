package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record AtendimentoClinicoRequestDTO(
        @NotNull
        Long animalId,

        @NotNull
        Long propriedadeId,

        LocalDateTime dataHora,

        @NotNull
        TipoAtendimento tipoAtendimento,

        String queixaPrincipal,
        String diagnosticoPresuntivo,
        String conduta,

        @Valid
        List<MedicacaoAplicadaRequestDTO> medicacoes
) {
}
