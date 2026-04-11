package com.reproequinos.vitaequus_api.auth.dtos;

public class TokenResponse {

    private String token;
    private String tipo = "Bearer";
    private Long veterinarioId;
    private String nome;
    private String email;

    public TokenResponse() {}

    public TokenResponse(String token, Long veterinarioId, String nome, String email) {
        this.token = token;
        this.veterinarioId = veterinarioId;
        this.nome = nome;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Long veterinarioId) { this.veterinarioId = veterinarioId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}