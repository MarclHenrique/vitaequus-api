package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.TipoDocumento;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb02Proprietario")
public class Proprietario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProprietario")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "nrdocumento", nullable = false, length = 20, unique = true)
    private String nrDocumento;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 100)
    private String email;

    @OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProprietarioPropriedade> proprietarioPropriedades = new ArrayList<>();

    @OneToMany(mappedBy = "proprietario", fetch = FetchType.LAZY)
    private List<Animal> animais = new ArrayList<>();

    public Proprietario() {
    }

    public Proprietario(Long id, String nome, TipoDocumento tipoDocumento, String nrDocumento,
                        String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.tipoDocumento = tipoDocumento;
        this.nrDocumento = nrDocumento;
        this.telefone = telefone;
        this.email = email;
    }

    public void adicionarVinculo(ProprietarioPropriedade vinculo) {
        this.proprietarioPropriedades.add(vinculo);
        vinculo.setProprietario(this);
    }

    public void removerVinculo(ProprietarioPropriedade vinculo) {
        this.proprietarioPropriedades.remove(vinculo);
        vinculo.setProprietario(null);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNrDocumento() {
        return nrDocumento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public List<ProprietarioPropriedade> getProprietarioPropriedades() {
        return proprietarioPropriedades;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setNrDocumento(String nrDocumento) {
        this.nrDocumento = nrDocumento;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProprietarioPropriedades(List<ProprietarioPropriedade> proprietarioPropriedades) {
        this.proprietarioPropriedades = proprietarioPropriedades;
    }

    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proprietario that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}