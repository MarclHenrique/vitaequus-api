package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.RacaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.StatusRacaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.RacaResponseDTO;
import com.reproequinos.vitaequus_api.services.RacaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/racas")
public class RacaController {

    private final RacaService service;

    public RacaController(RacaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<RacaResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(nome, status, pageable));
    }

    @PostMapping
    public ResponseEntity<RacaResponseDTO> criar(@RequestBody @Valid RacaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RacaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RacaRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RacaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid StatusRacaRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizarStatus(id, dto.status()));
    }
}
