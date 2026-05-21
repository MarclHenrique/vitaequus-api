package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Response.DashboardReprodutivoResponseDTO;
import com.reproequinos.vitaequus_api.services.DashboardReprodutivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reproducao")
@Tag(name = "Dashboard Reprodutivo", description = "Indicadores e alertas reprodutivos")
public class DashboardReprodutivoController {

    private final DashboardReprodutivoService dashboardReprodutivoService;

    public DashboardReprodutivoController(DashboardReprodutivoService dashboardReprodutivoService) {
        this.dashboardReprodutivoService = dashboardReprodutivoService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Consultar dashboard reprodutivo")
    public ResponseEntity<DashboardReprodutivoResponseDTO> buscarDashboard(
            @RequestParam(required = false) Long propriedadeId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dataReferencia
    ) {
        return ResponseEntity.ok(dashboardReprodutivoService.buscarDashboard(propriedadeId, dataReferencia));
    }
}
