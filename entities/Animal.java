package com.reproequinos.vitaequus_api.entities;

import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.Sexo;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb04Animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAnimal")
    private Long id;

    @Column(name = "identificacao", length = 100)
    private String identificacao;

    @Column(name = "nome", length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo")
    private Sexo sexo;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb23idRaca")
    private Raca raca;

    @Column(name = "pelagem", length = 80)
    private String pelagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb01idPropriedade")
    private Propriedade propriedade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb02idProprietario")
    private Proprietario proprietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb22idCuidadorPropriedade")
    private CuidadorPropriedade cuidadorPropriedade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb08idDoadora")
    private Doadora doadora;

    // Referência ao Produtor (pai) — entidade do módulo Reprodução
    @Column(name = "fktb09idProduto")
    private Long idProdutor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAnimal status;

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovimentacaoAnimal> movimentacoes;

    public Animal() {
    }

    public Animal(Long id, String identificacao, String nome, Categoria categoria, Sexo sexo,
                  LocalDate dataNascimento, Raca raca, String pelagem, Propriedade propriedade,
                  Proprietario proprietario, CuidadorPropriedade cuidadorPropriedade,
                  Doadora doadora, Long idProdutor, StatusAnimal status, String biografia,
                  List<MovimentacaoAnimal> movimentacoes) {
        this.id = id;
        this.identificacao = identificacao;
        this.nome = nome;
        this.categoria = categoria;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.raca = raca;
        this.pelagem = pelagem;
        this.propriedade = propriedade;
        this.proprietario = proprietario;
        this.cuidadorPropriedade = cuidadorPropriedade;
        this.doadora = doadora;
        this.idProdutor = idProdutor;
        this.status = status;
        this.biografia = biografia;
        this.movimentacoes = movimentacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Raca getRaca() {
        return raca;
    }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }

    public String getPelagem() {
        return pelagem;
    }

    public void setPelagem(String pelagem) {
        this.pelagem = pelagem;
    }

    public Propriedade getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(Propriedade propriedade) {
        this.propriedade = propriedade;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    public CuidadorPropriedade getCuidadorPropriedade() {
        return cuidadorPropriedade;
    }

    public void setCuidadorPropriedade(CuidadorPropriedade cuidadorPropriedade) {
        this.cuidadorPropriedade = cuidadorPropriedade;
    }

    public Doadora getDoadora() {
        return doadora;
    }

    public void setDoadora(Doadora doadora) {
        this.doadora = doadora;
    }

    public Long getIdProdutor() {
        return idProdutor;
    }

    public void setIdProdutor(Long idProdutor) {
        this.idProdutor = idProdutor;
    }

    public StatusAnimal getStatus() {
        return status;
    }

    public void setStatus(StatusAnimal status) {
        this.status = status;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public List<MovimentacaoAnimal> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoAnimal> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }
}