package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb01Propriedade")
public class Propriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPropriedade")
    private Long id;

    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_propriedade", nullable = false)
    private TipoPropriedade tipoPropriedade;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkidVeterinario", nullable = false)
    private Veterinario veterinario;

    @OneToMany(mappedBy = "propriedade", fetch = FetchType.LAZY)
    private List<Animal> animais = new ArrayList<>();

    @OneToMany(mappedBy = "propriedade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProprietarioPropriedade> proprietarioPropriedades = new ArrayList<>();

    public Propriedade() {
    }

    public Propriedade(Long id, String nome, TipoPropriedade tipoPropriedade, String endereco,
                       String cidade, String estado, String celular, String email, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.tipoPropriedade = tipoPropriedade;
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
        this.celular = celular;
        this.email = email;
        this.ativo = ativo;
    }

    public void adicionarVinculo(ProprietarioPropriedade vinculo) {
        this.proprietarioPropriedades.add(vinculo);
        vinculo.setPropriedade(this);
    }

    public void removerVinculo(ProprietarioPropriedade vinculo) {
        this.proprietarioPropriedades.remove(vinculo);
        vinculo.setPropriedade(null);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoPropriedade getTipoPropriedade() {
        return tipoPropriedade;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getCelular() {
        return celular;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public List<ProprietarioPropriedade> getProprietarioPropriedades() {
        return proprietarioPropriedades;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipoPropriedade(TipoPropriedade tipoPropriedade) {
        this.tipoPropriedade = tipoPropriedade;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }

    public void setProprietarioPropriedades(List<ProprietarioPropriedade> proprietarioPropriedades) {
        this.proprietarioPropriedades = proprietarioPropriedades;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Propriedade that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}