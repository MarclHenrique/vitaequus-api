package com.reproequinos.vitaequus_api.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CadastroRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    // Registro profissional é opcional
    @JsonProperty("registro_profissional")
    private String registroProfissional;

    private String telefone;

    @JsonProperty("base_cidade")
    private String cidadeBase;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String password;

    @JsonProperty("confirmar_senha")
    private String confirmarSenha;

    public CadastroRequest() {}

    public CadastroRequest(String nome, String registroProfissional, String telefone,
                           String email, String password, String cidadeBase) {
        this.nome = nome;
        this.registroProfissional = registroProfissional;
        this.telefone = telefone;
        this.email = email;
        this.password = password;
        this.cidadeBase = cidadeBase;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return password; }
    public void setSenha(String password) { this.password = password; }

    public String getCidadeBase() {
        return cidadeBase;
    }

    public void setCidadeBase(String cidadeBase) {
        this.cidadeBase = cidadeBase;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}