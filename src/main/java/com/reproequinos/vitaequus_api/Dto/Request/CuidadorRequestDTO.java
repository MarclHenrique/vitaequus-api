package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CuidadorRequestDTO(
        @NotBlank String nome,
        @NotNull TipoDocumento tipoDocumento,
        @NotBlank String nrDocumento,
        String telefone,
        String email
) {}