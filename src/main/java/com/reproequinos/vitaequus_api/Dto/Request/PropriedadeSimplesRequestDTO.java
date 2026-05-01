package com.reproequinos.vitaequus_api.Dto.Request;

import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PropriedadeSimplesRequestDTO {

    @NotBlank
    @Size(max = 80)
    private String nome;

    @NotNull
    private TipoPropriedade tipoPropriedade;

    private String endereco;
    private String cidade;

    @Size(max = 2)
    private String estado;

    @Size(max = 20)
    private String celular;

    @Size(max = 100)
    private String email;

    public String getNome() {
        return nome;
    }

    public TipoPropriedade getTipoPropriedade() {
        return tipoPropriedade;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getCelular() {
        return celular;
    }

    public String getEmail() {
        return email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipoPropriedade(TipoPropriedade tipoPropriedade) {
        this.tipoPropriedade = tipoPropriedade;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}