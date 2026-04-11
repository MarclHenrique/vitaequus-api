package com.reproequinos.vitaequus_api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb08Doadora")
public class Doadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDoadora")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fktb01idAnimal", nullable = false)
    private Animal animal;

    public Doadora() {}

    public Doadora(Long id, Animal animal) {
        this.id = id;
        this.animal = animal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
}