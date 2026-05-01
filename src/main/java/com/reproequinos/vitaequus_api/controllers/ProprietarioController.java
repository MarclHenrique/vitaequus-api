package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.ProprietarioRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VincularPropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.ProprietarioResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.VinculoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Resumo.PropriedadeResumoDTO;
import com.reproequinos.vitaequus_api.services.ProprietarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proprietarios")
public class ProprietarioController {

    private final ProprietarioService proprietarioService;

    public ProprietarioController(ProprietarioService proprietarioService) {
        this.proprietarioService = proprietarioService;
    }

    @GetMapping
    public ResponseEntity<List<ProprietarioResponseDTO>> listar() {
        return ResponseEntity.ok(proprietarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProprietarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proprietarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProprietarioResponseDTO> criar(@Valid @RequestBody ProprietarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proprietarioService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProprietarioResponseDTO> atualizar(@PathVariable Long id,
                                                             @Valid @RequestBody ProprietarioRequestDTO dto) {
        return ResponseEntity.ok(proprietarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        proprietarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/propriedades")
    public ResponseEntity<VinculoResponseDTO> vincularPropriedade(@PathVariable Long id,
                                                                  @Valid @RequestBody VincularPropriedadeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proprietarioService.vincularPropriedade(id, dto));
    }

    @DeleteMapping("/{id}/propriedades/{idProp}")
    public ResponseEntity<Void> removerVinculo(@PathVariable Long id, @PathVariable Long idProp) {
        proprietarioService.removerVinculo(id, idProp);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/propriedades")
    public ResponseEntity<List<PropriedadeResumoDTO>> listarPropriedades(@PathVariable Long id) {
        return ResponseEntity.ok(proprietarioService.listarPropriedadesDoProprietario(id));
    }
}