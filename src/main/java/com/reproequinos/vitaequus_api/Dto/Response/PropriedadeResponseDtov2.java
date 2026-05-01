package com.reproequinos.vitaequus_api.Dto.Response;

import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;

public class PropriedadeResponseDtov2 {
    private Long id;
    private String nome;
    private TipoPropriedade tipoPropriedade;
    private String endereco;
    private String cidade;
    private String estado;
    private String celular;
    private String email;
    private Boolean ativo;

    public Long getId() {
        return id;
    }

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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
