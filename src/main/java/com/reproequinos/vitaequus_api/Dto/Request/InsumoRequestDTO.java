package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Enum.UnidadeMedidaInsumo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record InsumoRequestDTO(
        @NotBlank
        @Size(max = 120)
        String nomeComercial,

        @NotNull
        TipoInsumo tipo,

        @Size(max = 120)
        String principioAtivo,

        @NotNull
        UnidadeMedidaInsumo unidadeMedida,

        @PositiveOrZero
        Integer estoqueAtual,

        @PositiveOrZero
        Integer estoqueMinimo,

        LocalDate dataValidade,

        @NotNull
        Long fornecedorId,

        @Size(max = 500)
        String observacoes
) {
}
