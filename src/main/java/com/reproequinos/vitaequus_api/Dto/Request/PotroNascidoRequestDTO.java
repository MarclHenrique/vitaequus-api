package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.CondicaoNeonato;
import com.reproequinos.vitaequus_api.entities.Enum.ResultadoPotro;
import com.reproequinos.vitaequus_api.entities.Enum.Sexo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PotroNascidoRequestDTO(
        String nome,
        String identificacao,

        @NotNull
        Sexo sexo,

        String pelagemInicial,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal pesoNascimento,

        @NotNull
        ResultadoPotro resultado,

        CondicaoNeonato condicaoNeonato,
        String observacoes
) {
}
