package com.reproequinos.vitaequus_api.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb06Veterinario")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVeterinario")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "registro_profissional", nullable = false, length = 30)
    private String registroProfissional;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "base_cidade", length = 80)
    private String baseCidade;

    @Column(name = "senha", nullable = false)
    private String password;

    @OneToMany(mappedBy = "veterinario", fetch = FetchType.LAZY)
    private List<RotaVeterinario> rotas;

    public Veterinario() {}

    public Veterinario(Long id, String nome, String registroProfissional, String telefone,
                       String email, String baseCidade, String password, List<RotaVeterinario> rotas) {
        this.id = id;
        this.nome = nome;
        this.registroProfissional = registroProfissional;
        this.telefone = telefone;
        this.email = email;
        this.baseCidade = baseCidade;
        this.password = password;
        this.rotas = rotas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBaseCidade() { return baseCidade; }
    public void setBaseCidade(String baseCidade) { this.baseCidade = baseCidade; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<RotaVeterinario> getRotas() { return rotas; }
    public void setRotas(List<RotaVeterinario> rotas) { this.rotas = rotas; }
}