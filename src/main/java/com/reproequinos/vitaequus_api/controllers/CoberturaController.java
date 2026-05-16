package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.CoberturaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.CoberturaUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CoberturaResponseDTO;
import com.reproequinos.vitaequus_api.services.CoberturaService;
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
@RequestMapping("/api/v1/coberturas")
public class CoberturaController {

    private final CoberturaService coberturaService;

    public CoberturaController(CoberturaService coberturaService) {
        this.coberturaService = coberturaService;
    }

    @GetMapping
    public ResponseEntity<Page<CoberturaResponseDTO>> listar(
            @RequestParam(required = false) Long doadoraAnimalId,
            @RequestParam(required = false) Long produtorAnimalId,
            @RequestParam(required = false) Long propriedadeId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                coberturaService.listar(doadoraAnimalId, produtorAnimalId, propriedadeId, dataInicio, dataFim, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoberturaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(coberturaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CoberturaResponseDTO> criar(
            @Valid @RequestBody CoberturaRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coberturaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoberturaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CoberturaUpdateDTO dto
    ) {
        return ResponseEntity.ok(coberturaService.atualizar(id, dto));
    }
}
