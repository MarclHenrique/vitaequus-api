package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.AtendimentoClinicoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MedicacaoAplicadaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.AtendimentoClinicoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.MedicacaoAplicadaResponseDTO;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import com.reproequinos.vitaequus_api.services.AtendimentoClinicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/atendimentos")
public class AtendimentoClinicoController {

    private final AtendimentoClinicoService atendimentoService;

    public AtendimentoClinicoController(AtendimentoClinicoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @GetMapping
    public ResponseEntity<Page<AtendimentoClinicoResponseDTO>> listarAtendimentos(
            @RequestParam(required = false) Long animalId,
            @RequestParam(required = false) TipoAtendimento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            Pageable pageable) {

        Page<AtendimentoClinicoResponseDTO> atendimentos = atendimentoService.listarAtendimentos(
                animalId, tipo, dataInicio, dataFim, pageable
        );

        return ResponseEntity.ok(atendimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoClinicoResponseDTO> detalharAtendimento(@PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.detalharAtendimento(id));
    }

    @PostMapping
    public ResponseEntity<AtendimentoClinicoResponseDTO> registrarAtendimento(
            @Valid @RequestBody AtendimentoClinicoRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.registrarAtendimento(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtendimentoClinicoResponseDTO> atualizarAtendimento(
            @PathVariable Long id,
            @Valid @RequestBody AtendimentoClinicoRequestDTO dto) {

        return ResponseEntity.ok(atendimentoService.atualizarAtendimento(id, dto));
    }

    @GetMapping("/{id}/medicacoes")
    public ResponseEntity<List<MedicacaoAplicadaResponseDTO>> listarMedicacoes(@PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.listarMedicacoes(id));
    }

    @PostMapping("/{id}/medicacoes")
    public ResponseEntity<MedicacaoAplicadaResponseDTO> adicionarMedicacao(
            @PathVariable Long id,
            @Valid @RequestBody MedicacaoAplicadaRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atendimentoService.adicionarMedicacao(id, dto));
    }

    @DeleteMapping("/{idAt}/medicacoes/{idMed}")
    public ResponseEntity<Void> removerMedicacao(
            @PathVariable Long idAt,
            @PathVariable Long idMed) {

        atendimentoService.removerMedicacao(idAt, idMed);
        return ResponseEntity.noContent().build();
    }
}