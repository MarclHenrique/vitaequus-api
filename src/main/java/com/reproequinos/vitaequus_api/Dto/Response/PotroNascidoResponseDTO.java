package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.CondicaoNeonato;
import com.reproequinos.vitaequus_api.entities.Enum.ResultadoPotro;
import com.reproequinos.vitaequus_api.entities.Enum.Sexo;

import java.math.BigDecimal;

public record PotroNascidoResponseDTO(
        Long id,
        Long partoId,
        Long animalIdCriado,
        String nome,
        String identificacao,
        Sexo sexo,
        String pelagemInicial,
        BigDecimal pesoNascimento,
        ResultadoPotro resultado,
        CondicaoNeonato condicaoNeonato,
        String observacoes
) {
}
