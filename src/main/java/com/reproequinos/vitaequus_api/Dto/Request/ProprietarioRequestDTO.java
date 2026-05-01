package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProprietarioRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    private TipoDocumento tipoDocumento;

    @NotBlank
    @Size(max = 20)
    private String nrDocumento;

    @Size(max = 20)
    private String telefone;

    @Size(max = 100)
    private String email;

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