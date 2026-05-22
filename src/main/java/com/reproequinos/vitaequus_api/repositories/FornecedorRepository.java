package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long>, JpaSpecificationExecutor<Fornecedor> {

    List<Fornecedor> findByVeterinarioId(Long veterinarioId);

    Optional<Fornecedor> findByIdAndVeterinarioId(Long id, Long veterinarioId);

    boolean existsByIdAndVeterinarioId(Long id, Long veterinarioId);
}
