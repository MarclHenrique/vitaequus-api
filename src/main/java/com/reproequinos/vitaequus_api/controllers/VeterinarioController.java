package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.AlterarSenhaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VeterinarioPerfilUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.VeterinarioPerfilResponseDTO;
import com.reproequinos.vitaequus_api.services.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/veterinarios")
@Tag(name = "Veterinarios / Perfil", description = "Perfil do veterinario logado")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar perfil logado")
    public ResponseEntity<VeterinarioPerfilResponseDTO> buscarPerfilLogado() {
        return ResponseEntity.ok(veterinarioService.buscarPerfilLogado());
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar perfil logado")
    public ResponseEntity<VeterinarioPerfilResponseDTO> atualizarPerfilLogado(
            @Valid @RequestBody VeterinarioPerfilUpdateDTO dto
    ) {
        return ResponseEntity.ok(veterinarioService.atualizarPerfilLogado(dto));
    }

    @PatchMapping("/me/senha")
    @Operation(summary = "Alterar senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaRequestDTO dto) {
        veterinarioService.alterarSenha(dto);
        return ResponseEntity.noContent().build();
    }
}
