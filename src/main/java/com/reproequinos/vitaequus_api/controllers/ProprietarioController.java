package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.ProprietarioRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VincularPropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.ProprietarioResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.VinculoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Resumo.PropriedadeResumoDTO;
import com.reproequinos.vitaequus_api.services.ProprietarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proprietarios")
@Tag(name = "Proprietarios", description = "Cadastro, consulta e vinculos de proprietarios")
public class ProprietarioController {

    private final ProprietarioService proprietarioService;

    public ProprietarioController(ProprietarioService proprietarioService) {
        this.proprietarioService = proprietarioService;
    }

    @GetMapping
    @Operation(summary = "Listar proprietarios")
    public ResponseEntity<Page<ProprietarioResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(proprietarioService.listar(nome, documento, email, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proprietario por ID")
    public ResponseEntity<ProprietarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proprietarioService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar proprietario")
    public ResponseEntity<ProprietarioResponseDTO> criar(@Valid @RequestBody ProprietarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proprietarioService.criar(dto));
    }

    @PatchMapping("/{id}/propriedades/{idProp}/tipo-vinculo")
    @Operation(summary = "Atualizar tipo de vinculo com propriedade")
    public ResponseEntity<VinculoResponseDTO> atualizarTipoVinculo(
            @PathVariable Long id,
            @PathVariable Long idProp,
            @RequestBody VincularPropriedadeRequestDTO dto
    ) {
        return ResponseEntity.ok(
                proprietarioService.atualizarTipoVinculo(id, idProp, dto)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar proprietario")
    public ResponseEntity<ProprietarioResponseDTO> atualizar(@PathVariable Long id,
                                                             @Valid @RequestBody ProprietarioRequestDTO dto) {
        return ResponseEntity.ok(proprietarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir proprietario")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        proprietarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/propriedades")
    @Operation(summary = "Vincular proprietario a propriedade")
    public ResponseEntity<VinculoResponseDTO> vincularPropriedade(@PathVariable Long id,
                                                                  @Valid @RequestBody VincularPropriedadeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proprietarioService.vincularPropriedade(id, dto));
    }

    @DeleteMapping("/{id}/propriedades/{idProp}")
    @Operation(summary = "Remover vinculo com propriedade")
    public ResponseEntity<Void> removerVinculo(@PathVariable Long id, @PathVariable Long idProp) {
        proprietarioService.removerVinculo(id, idProp);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/propriedades")
    @Operation(summary = "Listar propriedades do proprietario")
    public ResponseEntity<List<PropriedadeResumoDTO>> listarPropriedades(@PathVariable Long id) {
        return ResponseEntity.ok(proprietarioService.listarPropriedadesDoProprietario(id));
    }
}
