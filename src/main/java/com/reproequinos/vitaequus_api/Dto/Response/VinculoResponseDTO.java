package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoVinculo;

public class VinculoResponseDTO {

    private Long id;
    private Long idProprietario;
    private Long idPropriedade;
    private TipoVinculo tipoVinculo;

    public Long getId() {
        return id;
    }

    public Long getIdProprietario() {
        return idProprietario;
    }

    public Long getIdPropriedade() {
        return idPropriedade;
    }

    public TipoVinculo getTipoVinculo() {
        return tipoVinculo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdProprietario(Long idProprietario) {
        this.idProprietario = idProprietario;
    }

    public void setIdPropriedade(Long idPropriedade) {
        this.idPropriedade = idPropriedade;
    }

    public void setTipoVinculo(TipoVinculo tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }
}