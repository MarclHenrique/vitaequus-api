package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Proprietario;
import com.reproequinos.vitaequus_api.entities.ProprietarioPropriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {
    boolean existsByNrDocumento(String nrDocumento);
    boolean existsByNrDocumentoAndIdNot(String nrDocumento, Long id);
    Optional<Proprietario> findById(Long id);
}