package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Response.CheckupGestacionalResponseDTO;
import com.reproequinos.vitaequus_api.services.GestacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/checkups-gestacionais")
@Tag(name = "Check-ups Gestacionais", description = "Consultas globais de check-ups gestacionais")
public class CheckupGestacionalController {

    private final GestacaoService gestacaoService;

    public CheckupGestacionalController(GestacaoService gestacaoService) {
        this.gestacaoService = gestacaoService;
    }

    @GetMapping
    @Operation(summary = "Listar check-ups gestacionais")
    public ResponseEntity<Page<CheckupGestacionalResponseDTO>> listar(
            @RequestParam(required = false) Long gestacaoId,
            @RequestParam(required = false) String resultado,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                gestacaoService.listarCheckupsGlobais(gestacaoId, resultado, dataInicio, dataFim, pageable)
        );
    }
}
