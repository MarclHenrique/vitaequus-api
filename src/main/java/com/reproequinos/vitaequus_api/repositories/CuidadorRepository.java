package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Cuidador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuidadorRepository extends JpaRepository<Cuidador, Long> {

    boolean existsByNrDocumento(String nrDocumento);
}