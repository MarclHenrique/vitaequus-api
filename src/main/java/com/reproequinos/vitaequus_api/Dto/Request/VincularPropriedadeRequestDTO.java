package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoVinculo;
import jakarta.validation.constraints.NotNull;

public class VincularPropriedadeRequestDTO {

    @NotNull
    private Long idPropriedade;

    @NotNull
    private TipoVinculo tipoVinculo;

    public Long getIdPropriedade() {
        return idPropriedade;
    }

    public TipoVinculo getTipoVinculo() {
        return tipoVinculo;
    }

    public void setIdPropriedade(Long idPropriedade) {
        this.idPropriedade = idPropriedade;
    }

    public void setTipoVinculo(TipoVinculo tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }
}