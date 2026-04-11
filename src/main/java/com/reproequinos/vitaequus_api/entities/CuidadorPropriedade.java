package com.reproequinos.vitaequus_api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb22CuidadorPropriedade")
public class CuidadorPropriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCuidadorPropriedade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb21idCuidador", nullable = false)
    private Cuidador cuidador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb01idPropriedade", nullable = false)
    private Propriedade propriedade;

    /**
     * 0 = ativo (default), 1 = inativo
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    public CuidadorPropriedade() {}

    public CuidadorPropriedade(Long id, Cuidador cuidador, Propriedade propriedade, Integer status) {
        this.id = id;
        this.cuidador = cuidador;
        this.propriedade = propriedade;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cuidador getCuidador() { return cuidador; }
    public void setCuidador(Cuidador cuidador) { this.cuidador = cuidador; }

    public Propriedade getPropriedade() { return propriedade; }
    public void setPropriedade(Propriedade propriedade) { this.propriedade = propriedade; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}