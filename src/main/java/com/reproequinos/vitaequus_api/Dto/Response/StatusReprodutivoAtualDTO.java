package com.reproequinos.vitaequus_api.Dto.Response;

import java.util.List;

public record StatusReprodutivoAtualDTO(
        List<AnimalStatusReprodutivoDTO> vaziasAguardandoCio,
        List<AnimalStatusReprodutivoDTO> emAcompanhamentoFolicular,
        List<AnimalStatusReprodutivoDTO> aguardandoDiagnostico,
        List<AnimalStatusReprodutivoDTO> prenhes
) {}
