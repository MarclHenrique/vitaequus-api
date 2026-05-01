package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.PropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.PropriedadeSimplesRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDtov2;
import com.reproequinos.vitaequus_api.Dto.Resumo.ProprietarioResumoDTO;
import com.reproequinos.vitaequus_api.services.PropriedadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/propriedades")
public class PropriedadeController {

    private final PropriedadeService propriedadeService;

    public PropriedadeController(PropriedadeService propriedadeService) {
        this.propriedadeService = propriedadeService;
    }

    @GetMapping
    public ResponseEntity<List<PropriedadeResponseDTO>> listar() {
        return ResponseEntity.ok(propriedadeService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropriedadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(propriedadeService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PropriedadeResponseDTO> criar(@Valid @RequestBody PropriedadeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propriedadeService.criar(dto));
    }

    @PostMapping("/v2")
    public ResponseEntity<PropriedadeResponseDtov2> criarSimples(@Valid @RequestBody PropriedadeSimplesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propriedadeService.criarSimples(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropriedadeResponseDTO> atualizar(@PathVariable Long id,
                                                            @Valid @RequestBody PropriedadeRequestDTO dto) {
        return ResponseEntity.ok(propriedadeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        propriedadeService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/proprietarios")
    public ResponseEntity<List<ProprietarioResumoDTO>> listarProprietarios(@PathVariable Long id) {
        return ResponseEntity.ok(propriedadeService.listarProprietariosDaPropriedade(id));
    }
}