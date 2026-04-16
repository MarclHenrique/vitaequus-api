package com.vitaequus.domain.model;

import com.vitaequus.domain.enums.TipoAtendimentoEnum;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "atendimento_clinico")
public class AtendimentoClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atendimento", nullable = false, length = 25)
    private TipoAtendimentoEnum tipoAtendimento;

    @Lob
    @Column(name = "queixa_principal", columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Lob
    @Column(name = "diagnostico_presuntivo", columnDefinition = "TEXT")
    private String diagnosticoPresuntivo;

    @Lob
    @Column(name = "conduta", columnDefinition = "TEXT")
    private String conduta;

    public AtendimentoClinico() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }

    public Propriedade getPropriedade() { return propriedade; }
    public void setPropriedade(Propriedade propriedade) { this.propriedade = propriedade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public TipoAtendimentoEnum getTipoAtendimento() { return tipoAtendimento; }
    public void setTipoAtendimento(TipoAtendimentoEnum tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    public String getQueixaPrincipal() { return queixaPrincipal; }
    public void setQueixaPrincipal(String queixaPrincipal) { this.queixaPrincipal = queixaPrincipal; }

    public String getDiagnosticoPresuntivo() { return diagnosticoPresuntivo; }
    public void setDiagnosticoPresuntivo(String diagnosticoPresuntivo) { this.diagnosticoPresuntivo = diagnosticoPresuntivo; }

    public String getConduta() { return conduta; }
    public void setConduta(String conduta) { this.conduta = conduta; }
}
