package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.ViaAdministracao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MedicacaoAplicadaRequestDTO(
        @NotNull
        Long insumoId,

        @Size(max = 50)
        String dose,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal quantidadeAplicada,

        @NotNull
        ViaAdministracao viaAdministracao,

        String observacoes
) {
}
