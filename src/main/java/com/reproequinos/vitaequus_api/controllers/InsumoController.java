package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.InsumoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.InsumoResponseDTO;
import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.services.InsumoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/insumos")
@Tag(name = "Insumos", description = "Controle de insumos e estoque")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    @Operation(summary = "Listar insumos")
    public ResponseEntity<Page<InsumoResponseDTO>> listar(
            @RequestParam(required = false) TipoInsumo tipo,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) Boolean estoqueBaixo,
            @RequestParam(required = false) LocalDate vencendoAte,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(insumoService.listar(tipo, fornecedorId, estoqueBaixo, vencendoAte, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar insumo por ID")
    public ResponseEntity<InsumoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(insumoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar insumo")
    public ResponseEntity<InsumoResponseDTO> criar(@Valid @RequestBody InsumoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar insumo")
    public ResponseEntity<InsumoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody InsumoRequestDTO dto
    ) {
        return ResponseEntity.ok(insumoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir insumo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        insumoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
