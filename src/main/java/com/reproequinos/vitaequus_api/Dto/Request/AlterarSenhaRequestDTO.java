package com.reproequinos.vitaequus_api.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequestDTO(
        @NotBlank(message = "Senha atual e obrigatoria")
        String senhaAtual,

        @NotBlank(message = "Nova senha e obrigatoria")
        @Size(min = 6, message = "A nova senha deve ter no minimo 6 caracteres")
        String novaSenha
) {
}
