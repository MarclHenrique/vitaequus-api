package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.MovimentacaoAnimal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoAnimalRepository extends JpaRepository<MovimentacaoAnimal, Long> {

    List<MovimentacaoAnimal> findByAnimalIdOrderByDataMovimentacaoDesc(Long animalId);
}