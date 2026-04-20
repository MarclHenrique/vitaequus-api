package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;

import java.time.LocalDate;

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
@Table(name = "gestacao")
public class Gestacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doadora_id", nullable = false)
    private Doadora doadora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cobertura_id", nullable = false)
    private Cobertura cobertura;

    @Column(name = "data_diagnostico_inicial")
    private LocalDate dataDiagnosticoInicial;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 20)
    private ResultadoGestacao resultado;

    @Column(name = "data_previsao_parto")
    private LocalDate dataPrevisaoParto;

    @Lob
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public Gestacao() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Doadora getDoadora() { return doadora; }
    public void setDoadora(Doadora doadora) { this.doadora = doadora; }

    public Cobertura getCobertura() { return cobertura; }
    public void setCobertura(Cobertura cobertura) { this.cobertura = cobertura; }

    public LocalDate getDataDiagnosticoInicial() { return dataDiagnosticoInicial; }
    public void setDataDiagnosticoInicial(LocalDate dataDiagnosticoInicial) { this.dataDiagnosticoInicial = dataDiagnosticoInicial; }

    public ResultadoGestacao getResultado() { return resultado; }
    public void setResultado(ResultadoGestacao resultado) { this.resultado = resultado; }

    public LocalDate getDataPrevisaoParto() { return dataPrevisaoParto; }
    public void setDataPrevisaoParto(LocalDate dataPrevisaoParto) { this.dataPrevisaoParto = dataPrevisaoParto; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
