package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.AtendimentoClinicoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.AtendimentoClinicoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MedicacaoAplicadaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.AtendimentoClinicoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.MedicacaoAplicadaResponseDTO;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import com.reproequinos.vitaequus_api.services.AtendimentoClinicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/atendimentos")
@Tag(name = "Atendimento Clinico / Prontuario", description = "Atendimentos clinicos e medicacoes aplicadas")
public class AtendimentoClinicoController {

    private final AtendimentoClinicoService atendimentoService;

    public AtendimentoClinicoController(AtendimentoClinicoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @GetMapping
    @Operation(summary = "Listar atendimentos clinicos")
    public ResponseEntity<Page<AtendimentoClinicoResponseDTO>> listar(
            @RequestParam(required = false) Long animalId,
            @RequestParam(required = false) TipoAtendimento tipo,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(atendimentoService.listar(animalId, tipo, dataInicio, dataFim, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar atendimento clinico por ID")
    public ResponseEntity<AtendimentoClinicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar atendimento clinico")
    public ResponseEntity<AtendimentoClinicoResponseDTO> criar(
            @Valid @RequestBody AtendimentoClinicoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atendimentoService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar atendimento clinico")
    public ResponseEntity<AtendimentoClinicoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AtendimentoClinicoUpdateDTO dto
    ) {
        return ResponseEntity.ok(atendimentoService.atualizar(id, dto));
    }

    @GetMapping("/{id}/medicacoes")
    @Operation(summary = "Listar medicacoes do atendimento")
    public ResponseEntity<List<MedicacaoAplicadaResponseDTO>> listarMedicacoes(@PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.listarMedicacoes(id));
    }

    @PostMapping("/{id}/medicacoes")
    @Operation(summary = "Adicionar medicacao ao atendimento")
    public ResponseEntity<MedicacaoAplicadaResponseDTO> adicionarMedicacao(
            @PathVariable Long id,
            @Valid @RequestBody MedicacaoAplicadaRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atendimentoService.adicionarMedicacao(id, dto));
    }

    @DeleteMapping("/{idAt}/medicacoes/{idMed}")
    @Operation(summary = "Remover medicacao do atendimento")
    public ResponseEntity<Void> removerMedicacao(
            @PathVariable Long idAt,
            @PathVariable Long idMed
    ) {
        atendimentoService.removerMedicacao(idAt, idMed);
        return ResponseEntity.noContent().build();
    }
}
