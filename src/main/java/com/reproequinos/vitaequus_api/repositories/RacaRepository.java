package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Raca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RacaRepository extends JpaRepository<Raca, Long>, JpaSpecificationExecutor<Raca> {

    List<Raca> findByStatus(Integer status); // 0 = ativo

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Raca> findByIdAndStatus(Long id, Integer status);

}
