package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.ViaAdministracao;
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

import java.math.BigDecimal;

@Entity
@Table(name = "tb16MedicacaoAplicada")
public class MedicacaoAplicada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMedicacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fktb15idAtendimento", nullable = false)
    private AtendimentoClinico atendimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fktb18idInsumo", nullable = false)
    private Insumo insumo;

    @Column(name = "dose", length = 50)
    private String dose;

    @Column(name = "quantidade_aplicada", precision = 10, scale = 2)
    private BigDecimal quantidadeAplicada;

    @Enumerated(EnumType.STRING)
    @Column(name = "via_administracao", length = 50)
    private ViaAdministracao viaAdministracao;

    @Lob
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public MedicacaoAplicada() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AtendimentoClinico getAtendimento() { return atendimento; }
    public void setAtendimento(AtendimentoClinico atendimento) { this.atendimento = atendimento; }

    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public BigDecimal getQuantidadeAplicada() { return quantidadeAplicada; }
    public void setQuantidadeAplicada(BigDecimal quantidadeAplicada) { this.quantidadeAplicada = quantidadeAplicada; }

    public ViaAdministracao getViaAdministracao() { return viaAdministracao; }
    public void setViaAdministracao(ViaAdministracao viaAdministracao) { this.viaAdministracao = viaAdministracao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
