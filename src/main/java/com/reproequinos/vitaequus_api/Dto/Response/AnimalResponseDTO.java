package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.Sexo;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;

import java.time.LocalDate;

public record AnimalResponseDTO(
        Long id,
        String identificacao,
        String nome,
        Categoria categoria,
        Sexo sexo,
        LocalDate dataNascimento,
        Long racaId,
        String nomeRaca,
        String pelagem,
        Long propriedadeId,
        String nomePropriedade,
        Long proprietarioId,
        String nomeProprietario,
        StatusAnimal status,
        String biografia,
        String urlFoto
) {}