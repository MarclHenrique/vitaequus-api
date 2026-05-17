package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.CondicaoNeonato;
import com.reproequinos.vitaequus_api.entities.Enum.ResultadoPotro;


import java.math.BigDecimal;

import com.reproequinos.vitaequus_api.entities.Enum.Sexo;
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
@Table(name = "tb14PotroNascido")
public class PotroNascido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPotro")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fktb13idParto", nullable = false)
    private Parto parto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb04idAnimal")
    private Animal animalCriado;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, length = 10)
    private Sexo sexo;

    @Column(name = "pelagem_inicial", length = 80)
    private String pelagemInicial;

    @Column(name = "peso_nascimento", precision = 10, scale = 2)
    private BigDecimal pesoNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 15)
    private ResultadoPotro resultado;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao_neonato", length = 20)
    private CondicaoNeonato condicaoNeonato;

    @Lob
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public PotroNascido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Parto getParto() { return parto; }
    public void setParto(Parto parto) { this.parto = parto; }

    public Animal getAnimalCriado() { return animalCriado; }
    public void setAnimalCriado(Animal animalCriado) { this.animalCriado = animalCriado; }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }

    public String getPelagemInicial() { return pelagemInicial; }
    public void setPelagemInicial(String pelagemInicial) { this.pelagemInicial = pelagemInicial; }

    public BigDecimal getPesoNascimento() { return pesoNascimento; }
    public void setPesoNascimento(BigDecimal pesoNascimento) { this.pesoNascimento = pesoNascimento; }

    public ResultadoPotro getResultado() { return resultado; }
    public void setResultado(ResultadoPotro resultado) { this.resultado = resultado; }

    public CondicaoNeonato getCondicaoNeonato() { return condicaoNeonato; }
    public void setCondicaoNeonato(CondicaoNeonato condicaoNeonato) { this.condicaoNeonato = condicaoNeonato; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
