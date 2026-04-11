package com.reproequinos.vitaequus_api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb20RotaPropriedade")
public class RotaPropriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRotaPropriedade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb19idRota", nullable = false)
    private RotaVeterinario rota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb01idPropriedade", nullable = false)
    private Propriedade propriedade;

    @Column(name = "ordem_visita")
    private Integer ordemVisita;

    public RotaPropriedade() {}

    public RotaPropriedade(Long id, RotaVeterinario rota, Propriedade propriedade, Integer ordemVisita) {
        this.id = id;
        this.rota = rota;
        this.propriedade = propriedade;
        this.ordemVisita = ordemVisita;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RotaVeterinario getRota() { return rota; }
    public void setRota(RotaVeterinario rota) { this.rota = rota; }

    public Propriedade getPropriedade() { return propriedade; }
    public void setPropriedade(Propriedade propriedade) { this.propriedade = propriedade; }

    public Integer getOrdemVisita() { return ordemVisita; }
    public void setOrdemVisita(Integer ordemVisita) { this.ordemVisita = ordemVisita; }
}