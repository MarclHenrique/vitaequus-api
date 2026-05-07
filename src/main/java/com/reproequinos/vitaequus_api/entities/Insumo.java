package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "tb18Insumo",
        indexes = {
                @Index(name = "idx_insumo_veterinario", columnList = "fkidVeterinario"),
                @Index(name = "idx_insumo_fornecedor_veterinario", columnList = "fkidFornecedor, fkidVeterinario")
        }
)
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idInsumo")
    private Long id;

    @Column(name = "nome_comercial", nullable = false, length = 120)
    private String nomeComercial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoInsumo tipo;

    @Column(name = "principio_ativo", length = 120)
    private String principioAtivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkidFornecedor", nullable = false)
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkidVeterinario", nullable = false)
    private Veterinario veterinario;

    public Insumo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeComercial() { return nomeComercial; }
    public void setNomeComercial(String nomeComercial) { this.nomeComercial = nomeComercial; }

    public TipoInsumo getTipo() { return tipo; }
    public void setTipo(TipoInsumo tipo) { this.tipo = tipo; }

    public String getPrincipioAtivo() { return principioAtivo; }
    public void setPrincipioAtivo(String principioAtivo) { this.principioAtivo = principioAtivo; }

    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }
}
