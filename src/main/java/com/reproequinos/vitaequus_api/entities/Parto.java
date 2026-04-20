package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoParto;
import com.reproequinos.vitaequus_api.entities.Enum.TipoParto;

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
@Table(name = "parto")
public class Parto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gestacao_id", nullable = false)
    private Gestacao gestacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doadora_id", nullable = false)
    private Doadora doadora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_parto", nullable = false, length = 15)
    private TipoParto tipoParto;

    @Lob
    @Column(name = "intercorrencias", columnDefinition = "TEXT")
    private String intercorrencias;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 10)
    private ResultadoParto resultado;

    public Parto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Gestacao getGestacao() { return gestacao; }
    public void setGestacao(Gestacao gestacao) { this.gestacao = gestacao; }

    public Doadora getDoadora() { return doadora; }
    public void setDoadora(Doadora doadora) { this.doadora = doadora; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }

    public Propriedade getPropriedade() { return propriedade; }
    public void setPropriedade(Propriedade propriedade) { this.propriedade = propriedade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public TipoParto getTipoParto() { return tipoParto; }
    public void setTipoParto(TipoParto tipoParto) { this.tipoParto = tipoParto; }

    public String getIntercorrencias() { return intercorrencias; }
    public void setIntercorrencias(String intercorrencias) { this.intercorrencias = intercorrencias; }

    public ResultadoParto getResultado() { return resultado; }
    public void setResultado(ResultadoParto resultado) { this.resultado = resultado; }
}
