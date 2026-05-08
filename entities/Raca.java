package com.reproequinos.vitaequus_api.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb23Raca")
public class Raca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRaca")
    private Long id;

    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    /**
     * 0 = ativo (default), 1 = inativo
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    @OneToMany(mappedBy = "raca", fetch = FetchType.LAZY)
    private List<Animal> animais;

    public Raca() {}

    public Raca(Long id, String nome, Integer status, List<Animal> animais) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.animais = animais;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public List<Animal> getAnimais() { return animais; }
    public void setAnimais(List<Animal> animais) { this.animais = animais; }
}