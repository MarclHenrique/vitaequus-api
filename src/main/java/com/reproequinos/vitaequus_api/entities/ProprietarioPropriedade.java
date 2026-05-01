package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.TipoVinculo;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "tb03Proprietario_Propriedade",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_proprietario_propriedade", columnNames = {"fkidProprietario", "fkidPropriedade"})
        }
)
public class ProprietarioPropriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProprietario_Propriedade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkidProprietario", nullable = false)
    private Proprietario proprietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkidPropriedade", nullable = false)
    private Propriedade propriedade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vinculo", nullable = false, length = 50)
    private TipoVinculo tipoVinculo;

    public ProprietarioPropriedade() {
    }

    public ProprietarioPropriedade(Long id, Proprietario proprietario, Propriedade propriedade, TipoVinculo tipoVinculo) {
        this.id = id;
        this.proprietario = proprietario;
        this.propriedade = propriedade;
        this.tipoVinculo = tipoVinculo;
    }

    public Long getId() {
        return id;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public Propriedade getPropriedade() {
        return propriedade;
    }

    public TipoVinculo getTipoVinculo() {
        return tipoVinculo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    public void setPropriedade(Propriedade propriedade) {
        this.propriedade = propriedade;
    }

    public void setTipoVinculo(TipoVinculo tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProprietarioPropriedade that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}