package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoDocumento;

public class ProprietarioResponseDTO {

    private Long id;
    private String nome;
    private TipoDocumento tipoDocumento;
    private String nrDocumento;
    private String telefone;
    private String email;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNrDocumento() {
        return nrDocumento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setNrDocumento(String nrDocumento) {
        this.nrDocumento = nrDocumento;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}