package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Raca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RacaRepository extends JpaRepository<Raca, Long> {

    List<Raca> findByStatus(Integer status); // 0 = ativo

    boolean existsByNomeIgnoreCase(String nome);
}