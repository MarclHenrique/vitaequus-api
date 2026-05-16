package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.ExameReprodutivoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.ExameReprodutivoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.ExameReprodutivoResponseDTO;
import com.reproequinos.vitaequus_api.services.ExameReprodutivoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/exames-reprodutivos")
public class ExameReprodutivoController {

    private final ExameReprodutivoService exameService;

    public ExameReprodutivoController(ExameReprodutivoService exameService) {
        this.exameService = exameService;
    }

    @GetMapping
    public ResponseEntity<Page<ExameReprodutivoResponseDTO>> listar(
            @RequestParam(required = false) Long animalId,
            @RequestParam(required = false) Long propriedadeId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                exameService.listar(animalId, propriedadeId, dataInicio, dataFim, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameReprodutivoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(exameService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ExameReprodutivoResponseDTO> criar(
            @Valid @RequestBody ExameReprodutivoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exameService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExameReprodutivoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExameReprodutivoUpdateDTO dto
    ) {
        return ResponseEntity.ok(exameService.atualizar(id, dto));
    }
}
