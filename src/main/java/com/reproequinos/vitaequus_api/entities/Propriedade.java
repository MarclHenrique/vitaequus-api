package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;
import jakarta.persistence.*;

import java.util.List;

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

    @OneToMany(mappedBy = "propriedade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Animal> animais;

    @OneToMany(mappedBy = "propriedade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProprietarioPropriedade> proprietarioPropriedades;

    public Propriedade() {
    }

    public Propriedade(Long id, String nome, TipoPropriedade tipoPropriedade, String endereco,
                       String cidade, String estado, String celular, String email,
                       List<Animal> animais, List<ProprietarioPropriedade> proprietarioPropriedades) {
        this.id = id;
        this.nome = nome;
        this.tipoPropriedade = tipoPropriedade;
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
        this.celular = celular;
        this.email = email;
        this.animais = animais;
        this.proprietarioPropriedades = proprietarioPropriedades;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoPropriedade getTipoPropriedade() {
        return tipoPropriedade;
    }

    public void setTipoPropriedade(TipoPropriedade tipoPropriedade) {
        this.tipoPropriedade = tipoPropriedade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }

    public List<ProprietarioPropriedade> getProprietarioPropriedades() {
        return proprietarioPropriedades;
    }

    public void setProprietarioPropriedades(List<ProprietarioPropriedade> v) {
        this.proprietarioPropriedades = v;
    }
}