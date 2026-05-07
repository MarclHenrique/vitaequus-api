package com.reproequinos.vitaequus_api.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FornecedorRequestDTO(
        @NotBlank
        @Size(max = 120)
        String nome,

        @Size(max = 120)
        String contato,

        @Size(max = 20)
        String telefone,

        @Size(max = 120)
        String email
) {
}
