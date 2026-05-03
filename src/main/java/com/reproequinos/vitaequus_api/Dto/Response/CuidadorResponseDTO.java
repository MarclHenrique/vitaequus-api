package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoDocumento;

public record CuidadorResponseDTO(
        Long id,
        String nome,
        TipoDocumento tipoDocumento,
        String nrDocumento,
        String telefone,
        String email
) {}