package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.TipoDocumento;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb21Cuidador")
public class Cuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCuidador")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "nrdocumento", nullable = false, length = 20)
    private String nrDocumento;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 100)
    private String email;

    @OneToMany(mappedBy = "cuidador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CuidadorPropriedade> propriedades;

    public Cuidador() {}

    public Cuidador(Long id, String nome, TipoDocumento tipoDocumento, String nrDocumento,
                    String telefone, String email, List<CuidadorPropriedade> propriedades) {
        this.id = id;
        this.nome = nome;
        this.tipoDocumento = tipoDocumento;
        this.nrDocumento = nrDocumento;
        this.telefone = telefone;
        this.email = email;
        this.propriedades = propriedades;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNrDocumento() { return nrDocumento; }
    public void setNrDocumento(String nrDocumento) { this.nrDocumento = nrDocumento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<CuidadorPropriedade> getPropriedades() { return propriedades; }
    public void setPropriedades(List<CuidadorPropriedade> propriedades) { this.propriedades = propriedades; }

}