package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Enum.UnidadeMedidaInsumo;

import java.time.LocalDate;

public record InsumoResponseDTO(
        Long id,
        String nomeComercial,
        TipoInsumo tipo,
        String principioAtivo,
        UnidadeMedidaInsumo unidadeMedida,
        Integer estoqueAtual,
        Integer estoqueMinimo,
        LocalDate dataValidade,
        Long fornecedorId,
        String fornecedorNome,
        String observacoes
) {
}
