package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.CuidadorRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.StatusVinculoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VincularCuidadorPropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CuidadorPropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CuidadorResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.services.CuidadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuidadores")
public class CuidadorController {

    private final CuidadorService service;

    public CuidadorController(CuidadorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CuidadorResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<CuidadorResponseDTO> criar(@RequestBody @Valid CuidadorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuidadorResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CuidadorRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PostMapping("/{id}/propriedades")
    public ResponseEntity<CuidadorPropriedadeResponseDTO> vincularPropriedade(
            @PathVariable Long id,
            @RequestBody @Valid VincularCuidadorPropriedadeRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincularPropriedade(id, dto));
    }

    @PatchMapping("/{idC}/propriedades/{idP}/status")
    public ResponseEntity<CuidadorPropriedadeResponseDTO> atualizarStatusVinculo(
            @PathVariable Long idC,
            @PathVariable Long idP,
            @RequestBody @Valid StatusVinculoRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizarStatusVinculo(idC, idP, dto));
    }

    @GetMapping("/{id}/propriedades")
    public ResponseEntity<List<PropriedadeResponseDTO>> listarPropriedades(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.listarPropriedadesDoCuidador(id));
    }
}