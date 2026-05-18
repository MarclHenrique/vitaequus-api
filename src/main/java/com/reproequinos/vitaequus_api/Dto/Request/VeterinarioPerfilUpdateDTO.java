package com.reproequinos.vitaequus_api.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VeterinarioPerfilUpdateDTO(
        @NotBlank(message = "Nome e obrigatorio")
        String nome,

        String telefone,

        @NotBlank(message = "E-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        String email,

        String baseCidade
) {
}
