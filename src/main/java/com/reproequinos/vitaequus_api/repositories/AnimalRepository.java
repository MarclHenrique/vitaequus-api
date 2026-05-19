package com.reproequinos.vitaequus_api.repositories;

import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Page<Animal> findByCategoriaAndStatusAndPropriedadeId(
            Categoria categoria,
            StatusAnimal status,
            Long propriedadeId,
            Pageable pageable
    );

    Page<Animal> findByCategoria(Categoria categoria, Pageable pageable);

    Page<Animal> findByStatus(StatusAnimal status, Pageable pageable);

    Page<Animal> findByPropriedadeId(Long propriedadeId, Pageable pageable);

    @EntityGraph(attributePaths = {"raca", "propriedade", "proprietario"})
    Page<Animal> findByPropriedadeVeterinarioId(Long veterinarioId, Pageable pageable);

    @EntityGraph(attributePaths = {"raca", "propriedade", "proprietario"})
    Optional<Animal> findByIdAndPropriedadeVeterinarioId(Long id, Long veterinarioId);

    @EntityGraph(attributePaths = {"raca", "propriedade", "proprietario"})
    Page<Animal> findByCategoriaAndPropriedadeVeterinarioId(
            Categoria categoria,
            Long veterinarioId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"raca", "propriedade", "proprietario"})
    Page<Animal> findByStatusAndPropriedadeVeterinarioId(
            StatusAnimal status,
            Long veterinarioId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"raca", "propriedade", "proprietario"})
    Page<Animal> findByPropriedadeIdAndPropriedadeVeterinarioId(
            Long propriedadeId,
            Long veterinarioId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"propriedade"})
    @Query("""
            select a
            from Animal a
            where a.propriedade.veterinario.id = :veterinarioId
              and a.categoria in :categorias
              and a.status = com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal.ATIVO
              and (:propriedadeId is null or a.propriedade.id = :propriedadeId)
            order by a.nome asc
            """)
    List<Animal> findReprodutivoDashboardAnimals(
            @Param("veterinarioId") Long veterinarioId,
            @Param("categorias") Collection<Categoria> categorias,
            @Param("propriedadeId") Long propriedadeId
    );
}
