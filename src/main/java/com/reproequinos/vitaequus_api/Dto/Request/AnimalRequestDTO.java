package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.Sexo;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AnimalRequestDTO(

        String identificacao,

        @NotBlank
        String nome,

        @NotNull
        Categoria categoria,

        @NotNull
        Sexo sexo,

        LocalDate dataNascimento,

        Long racaId,

        String pelagem,

        @NotNull
        Long propriedadeId,

        Long proprietarioId,

        Long cuidadorPropriedadeId,

        StatusAnimal status,

        String biografia
) {}