package com.reproequinos.vitaequus_api.Dto.Response;

public record FornecedorResponseDTO(
        Long id,
        String nome,
        String contato,
        String telefone,
        String email
) {
}
