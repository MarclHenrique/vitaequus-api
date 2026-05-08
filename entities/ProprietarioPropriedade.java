package com.reproequinos.vitaequus_api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb03Proprietario_Propriedade")
public class ProprietarioPropriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProprietario_Propriedade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkidProprietario", nullable = false)
    private Proprietario proprietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkidPropriedade", nullable = false)
    private Propriedade propriedade;

    @Column(name = "tipo_vinculo", length = 50)
    private String tipoVinculo;

    public ProprietarioPropriedade() {}

    public ProprietarioPropriedade(Long id, Proprietario proprietario, Propriedade propriedade, String tipoVinculo) {
        this.id = id;
        this.proprietario = proprietario;
        this.propriedade = propriedade;
        this.tipoVinculo = tipoVinculo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Proprietario getProprietario() { return proprietario; }
    public void setProprietario(Proprietario proprietario) { this.proprietario = proprietario; }

    public Propriedade getPropriedade() { return propriedade; }
    public void setPropriedade(Propriedade propriedade) { this.propriedade = propriedade; }

    public String getTipoVinculo() { return tipoVinculo; }
    public void setTipoVinculo(String tipoVinculo) { this.tipoVinculo = tipoVinculo; }
}