package com.reproequinos.vitaequus_api.Dto.Response;

public record VeterinarioPerfilResponseDTO(
        Long id,
        String nome,
        String registroProfissional,
        String telefone,
        String email,
        String baseCidade,
        String role
) {
}
