package com.reproequinos.vitaequus_api.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb19RotaVeterinario")
public class RotaVeterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRota")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkidVeterinario", nullable = false)
    private Veterinario veterinario;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "rota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RotaPropriedade> propriedades;

    public RotaVeterinario() {}

    public RotaVeterinario(Long id, Veterinario veterinario, LocalDate dataInicio,
                           LocalDate dataFim, String observacoes, List<RotaPropriedade> propriedades) {
        this.id = id;
        this.veterinario = veterinario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.observacoes = observacoes;
        this.propriedades = propriedades;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public List<RotaPropriedade> getPropriedades() { return propriedades; }
    public void setPropriedades(List<RotaPropriedade> propriedades) { this.propriedades = propriedades; }
}