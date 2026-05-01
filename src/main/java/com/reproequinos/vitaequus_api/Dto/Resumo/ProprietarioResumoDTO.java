package com.reproequinos.vitaequus_api.Dto.Resumo;

public class ProprietarioResumoDTO {

    private Long id;
    private String nome;
    private String nrDocumento;
    private String email;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNrDocumento() {
        return nrDocumento;
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

    public void setNrDocumento(String nrDocumento) {
        this.nrDocumento = nrDocumento;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}