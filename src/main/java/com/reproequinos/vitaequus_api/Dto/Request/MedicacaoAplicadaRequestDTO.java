package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.ViaAdministracao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicacaoAplicadaRequestDTO(
        @NotNull
        Long insumoId,

        @Size(max = 50)
        String dose,

        @NotNull
        ViaAdministracao viaAdministracao,

        String observacoes
) {
}
