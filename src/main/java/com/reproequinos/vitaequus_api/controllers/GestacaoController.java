package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.CheckupGestacionalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.CheckupGestacionalUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Request.GestacaoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.GestacaoResultadoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CheckupGestacionalResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.GestacaoResponseDTO;
import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao;
import com.reproequinos.vitaequus_api.services.GestacaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/gestacoes")
public class GestacaoController {

    private final GestacaoService gestacaoService;

    public GestacaoController(GestacaoService gestacaoService) {
        this.gestacaoService = gestacaoService;
    }

    @GetMapping
    public ResponseEntity<Page<GestacaoResponseDTO>> listar(
            @RequestParam(required = false) Long doadoraId,
            @RequestParam(required = false) Long coberturaId,
            @RequestParam(required = false) ResultadoGestacao resultado,
            @RequestParam(required = false) StatusGestacao status,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dataFim,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                gestacaoService.listar(doadoraId, coberturaId, resultado, status, dataInicio, dataFim, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GestacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gestacaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<GestacaoResponseDTO> criar(@Valid @RequestBody GestacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gestacaoService.criar(dto));
    }

    @PatchMapping("/{id}/resultado")
    public ResponseEntity<GestacaoResponseDTO> atualizarResultado(
            @PathVariable Long id,
            @Valid @RequestBody GestacaoResultadoUpdateDTO dto
    ) {
        return ResponseEntity.ok(gestacaoService.atualizarResultado(id, dto));
    }

    @GetMapping("/{id}/checkups")
    public ResponseEntity<Page<CheckupGestacionalResponseDTO>> listarCheckups(
            @PathVariable Long id,
            @RequestParam(required = false) String resultado,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            Pageable pageable
    ) {
        return ResponseEntity.ok(gestacaoService.listarCheckups(id, resultado, dataInicio, dataFim, pageable));
    }

    @PostMapping("/{id}/checkups")
    public ResponseEntity<CheckupGestacionalResponseDTO> criarCheckup(
            @PathVariable Long id,
            @RequestBody CheckupGestacionalRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gestacaoService.criarCheckup(id, dto));
    }

    @PutMapping("/{idG}/checkups/{idC}")
    public ResponseEntity<CheckupGestacionalResponseDTO> atualizarCheckup(
            @PathVariable Long idG,
            @PathVariable Long idC,
            @RequestBody CheckupGestacionalUpdateDTO dto
    ) {
        return ResponseEntity.ok(gestacaoService.atualizarCheckup(idG, idC, dto));
    }
}
