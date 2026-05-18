package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Response.CheckupGestacionalResponseDTO;
import com.reproequinos.vitaequus_api.services.GestacaoService;
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
public class CheckupGestacionalController {

    private final GestacaoService gestacaoService;

    public CheckupGestacionalController(GestacaoService gestacaoService) {
        this.gestacaoService = gestacaoService;
    }

    @GetMapping
    public ResponseEntity<Page<CheckupGestacionalResponseDTO>> listar(
            @RequestParam(required = false) Long gestacaoId,
            @RequestParam(required = false) String resultado,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                gestacaoService.listarCheckupsGlobais(gestacaoId, resultado, dataInicio, dataFim, pageable)
        );
    }
}
