package com.vitaequus.domain.model;

import com.vitaequus.domain.enums.ResultadoPotroEnum;
import com.vitaequus.domain.enums.SexoAnimalEnum;

import java.math.BigDecimal;

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
@Table(name = "potro_nascido")
public class PotroNascido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parto_id", nullable = false)
    private Parto parto;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, length = 10)
    private SexoAnimalEnum sexo;

    @Column(name = "peso_nascimento", precision = 10, scale = 2)
    private BigDecimal pesoNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 15)
    private ResultadoPotroEnum resultado;

    @Lob
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public PotroNascido() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Parto getParto() { return parto; }
    public void setParto(Parto parto) { this.parto = parto; }

    public SexoAnimalEnum getSexo() { return sexo; }
    public void setSexo(SexoAnimalEnum sexo) { this.sexo = sexo; }

    public BigDecimal getPesoNascimento() { return pesoNascimento; }
    public void setPesoNascimento(BigDecimal pesoNascimento) { this.pesoNascimento = pesoNascimento; }

    public ResultadoPotroEnum getResultado() { return resultado; }
    public void setResultado(ResultadoPotroEnum resultado) { this.resultado = resultado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
