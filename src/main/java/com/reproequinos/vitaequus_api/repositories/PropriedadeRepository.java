package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long>, JpaSpecificationExecutor<Propriedade> {
    List<Propriedade> findByAtivoTrueAndVeterinarioId(Long veterinarioId);

    Optional<Propriedade> findByIdAndAtivoTrueAndVeterinarioId(Long id, Long veterinarioId);
}
