package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.PartoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.PartoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Request.PotroNascidoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PartoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PotroNascidoResponseDTO;
import com.reproequinos.vitaequus_api.services.PartoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/partos")
@Tag(name = "Partos / Potros", description = "Registros de partos e potros nascidos")
public class PartoController {

    private final PartoService partoService;

    public PartoController(PartoService partoService) {
        this.partoService = partoService;
    }

    @GetMapping
    @Operation(summary = "Listar partos")
    public ResponseEntity<Page<PartoResponseDTO>> listar(
            @RequestParam(required = false) Long gestacaoId,
            @RequestParam(required = false) Long doadoraId,
            @RequestParam(required = false) Long propriedadeId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                partoService.listar(gestacaoId, doadoraId, propriedadeId, dataInicio, dataFim, pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar parto por ID")
    public ResponseEntity<PartoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(partoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar parto")
    public ResponseEntity<PartoResponseDTO> criar(@Valid @RequestBody PartoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partoService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar parto")
    public ResponseEntity<PartoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PartoUpdateDTO dto
    ) {
        return ResponseEntity.ok(partoService.atualizar(id, dto));
    }

    @GetMapping("/{id}/potros")
    @Operation(summary = "Listar potros do parto")
    public ResponseEntity<List<PotroNascidoResponseDTO>> listarPotros(@PathVariable Long id) {
        return ResponseEntity.ok(partoService.listarPotros(id));
    }

    @PostMapping("/{id}/potros")
    @Operation(summary = "Adicionar potro ao parto")
    public ResponseEntity<PotroNascidoResponseDTO> adicionarPotro(
            @PathVariable Long id,
            @Valid @RequestBody PotroNascidoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partoService.adicionarPotro(id, dto));
    }

    @PutMapping("/{idParto}/potros/{idPotro}")
    @Operation(summary = "Atualizar potro do parto")
    public ResponseEntity<PotroNascidoResponseDTO> atualizarPotro(
            @PathVariable Long idParto,
            @PathVariable Long idPotro,
            @Valid @RequestBody PotroNascidoRequestDTO dto
    ) {
        return ResponseEntity.ok(partoService.atualizarPotro(idParto, idPotro, dto));
    }
}
