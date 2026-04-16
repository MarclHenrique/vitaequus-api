package com.vitaequus.domain.model;

import com.vitaequus.domain.enums.TipoProcedimentoEnum;
import com.vitaequus.domain.enums.TipoSemenEnum;

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
@Table(name = "cobertura")
public class Cobertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doadora_id", nullable = false)
    private Doadora doadora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produtor_id", nullable = false)
    private Produtor produtor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_procedimento", nullable = false, length = 20)
    private TipoProcedimentoEnum tipoProcedimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_semen", length = 15)
    private TipoSemenEnum tipoSemen;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Lob
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public Cobertura() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Doadora getDoadora() { return doadora; }
    public void setDoadora(Doadora doadora) { this.doadora = doadora; }

    public Produtor getProdutor() { return produtor; }
    public void setProdutor(Produtor produtor) { this.produtor = produtor; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }

    public Propriedade getPropriedade() { return propriedade; }
    public void setPropriedade(Propriedade propriedade) { this.propriedade = propriedade; }

    public TipoProcedimentoEnum getTipoProcedimento() { return tipoProcedimento; }
    public void setTipoProcedimento(TipoProcedimentoEnum tipoProcedimento) { this.tipoProcedimento = tipoProcedimento; }

    public TipoSemenEnum getTipoSemen() { return tipoSemen; }
    public void setTipoSemen(TipoSemenEnum tipoSemen) { this.tipoSemen = tipoSemen; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
