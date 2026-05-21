package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.CuidadorRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.StatusVinculoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VincularCuidadorPropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CuidadorPropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CuidadorResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.services.CuidadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuidadores")
@Tag(name = "Cuidadores", description = "Cadastro de cuidadores e vinculos com propriedades")
public class CuidadorController {

    private final CuidadorService service;

    public CuidadorController(CuidadorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar cuidadores")
    public ResponseEntity<Page<CuidadorResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) Boolean ativo,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(nome, telefone, ativo, pageable));
    }

    @PostMapping
    @Operation(summary = "Cadastrar cuidador")
    public ResponseEntity<CuidadorResponseDTO> criar(@RequestBody @Valid CuidadorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cuidador")
    public ResponseEntity<CuidadorResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CuidadorRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PostMapping("/{id}/propriedades")
    @Operation(summary = "Vincular cuidador a propriedade")
    public ResponseEntity<CuidadorPropriedadeResponseDTO> vincularPropriedade(
            @PathVariable Long id,
            @RequestBody @Valid VincularCuidadorPropriedadeRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincularPropriedade(id, dto));
    }

    @PatchMapping("/{idC}/propriedades/{idP}/status")
    @Operation(summary = "Atualizar status do vinculo do cuidador")
    public ResponseEntity<CuidadorPropriedadeResponseDTO> atualizarStatusVinculo(
            @PathVariable Long idC,
            @PathVariable Long idP,
            @RequestBody @Valid StatusVinculoRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizarStatusVinculo(idC, idP, dto));
    }

    @GetMapping("/{id}/propriedades")
    @Operation(summary = "Listar propriedades do cuidador")
    public ResponseEntity<List<PropriedadeResponseDTO>> listarPropriedades(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.listarPropriedadesDoCuidador(id));
    }
}
