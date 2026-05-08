package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb15AtendimentoClinico", indexes = {
        @Index(name = "idx_atendimento_veterinario", columnList = "fktb06idVeterinario"),
        @Index(name = "idx_atendimento_animal", columnList = "fktb04idAnimal")
})
public class AtendimentoClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAtendimento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb04idAnimal", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb06idVeterinario", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb01idPropriedade", nullable = false)
    private Propriedade propriedade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atendimento", nullable = false, length = 25)
    private TipoAtendimento tipoAtendimento;

    @Column(name = "queixa_principal", columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column(name = "diagnostico_presuntivo", columnDefinition = "TEXT")
    private String diagnosticoPresuntivo;

    @Column(name = "conduta", columnDefinition = "TEXT")
    private String conduta;

    public AtendimentoClinico() {}

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

    public TipoAtendimento getTipoAtendimento() { return tipoAtendimento; }
    public void setTipoAtendimento(TipoAtendimento tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    public String getQueixaPrincipal() { return queixaPrincipal; }
    public void setQueixaPrincipal(String queixaPrincipal) { this.queixaPrincipal = queixaPrincipal; }

    public String getDiagnosticoPresuntivo() { return diagnosticoPresuntivo; }
    public void setDiagnosticoPresuntivo(String diagnosticoPresuntivo) { this.diagnosticoPresuntivo = diagnosticoPresuntivo; }

    public String getConduta() { return conduta; }
    public void setConduta(String conduta) { this.conduta = conduta; }
}