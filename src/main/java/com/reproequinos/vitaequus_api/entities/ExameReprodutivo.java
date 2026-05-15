package com.reproequinos.vitaequus_api.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb07ExameReprodutivo")
public class ExameReprodutivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idExame")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fktb04idAnimal", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fktb06idVeterinario", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fktb01idPropriedade", nullable = false)
    private Propriedade propriedade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "diametro_folicular", precision = 10, scale = 2)
    private BigDecimal diametroFolicular;

    @Column(name = "edema_uterino", length = 100)
    private String edemaUterino;

    @Column(name = "corpo_luteo", length = 100)
    private String corpoLuteo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb18idInsumo")
    private Insumo insumo;

    @Lob
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public ExameReprodutivo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }

    public Propriedade getPropriedade() { return propriedade; }
    public void setPropriedade(Propriedade propriedade) { this.propriedade = propriedade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public BigDecimal getDiametroFolicular() { return diametroFolicular; }
    public void setDiametroFolicular(BigDecimal diametroFolicular) { this.diametroFolicular = diametroFolicular; }

    public String getEdemaUterino() { return edemaUterino; }
    public void setEdemaUterino(String edemaUterino) { this.edemaUterino = edemaUterino; }

    public String getCorpoLuteo() { return corpoLuteo; }
    public void setCorpoLuteo(String corpoLuteo) { this.corpoLuteo = corpoLuteo; }

    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
