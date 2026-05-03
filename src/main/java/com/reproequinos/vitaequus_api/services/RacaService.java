package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.RacaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.StatusRacaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.RacaResponseDTO;
import com.reproequinos.vitaequus_api.entities.Raca;
import com.reproequinos.vitaequus_api.repositories.RacaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RacaService {

    private final RacaRepository repository;

    public RacaService(RacaRepository repository) {
        this.repository = repository;
    }

    public List<RacaResponseDTO> listarAtivas() {
        return repository.findByStatus(0)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RacaResponseDTO criar(RacaRequestDTO dto) {

        if (repository.existsByNomeIgnoreCase(dto.nome())) {
            throw new RuntimeException("Raça já cadastrada");
        }

        Raca raca = new Raca();
        raca.setNome(dto.nome());
        raca.setStatus(0);

        return toResponse(repository.save(raca));
    }

    @Transactional
    public RacaResponseDTO atualizar(Long id, RacaRequestDTO dto) {

        Raca raca = buscar(id);

        raca.setNome(dto.nome());

        return toResponse(raca);
    }

    @Transactional
    public RacaResponseDTO atualizarStatus(Long id, Integer status) {

        if (status != 0 && status != 1) {
            throw new RuntimeException("Status inválido");
        }

        Raca raca = buscar(id);

        raca.setStatus(status);

        return toResponse(raca);
    }

    private Raca buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raça não encontrada"));
    }

    private RacaResponseDTO toResponse(Raca r) {
        return new RacaResponseDTO(
                r.getId(),
                r.getNome(),
                r.getStatus()
        );
    }
}