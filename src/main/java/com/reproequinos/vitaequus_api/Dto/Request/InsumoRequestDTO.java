package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InsumoRequestDTO(
        @NotBlank
        @Size(max = 120)
        String nomeComercial,

        @NotNull
        TipoInsumo tipo,

        @Size(max = 120)
        String principioAtivo,

        @NotNull
        Long fornecedorId
) {
}
