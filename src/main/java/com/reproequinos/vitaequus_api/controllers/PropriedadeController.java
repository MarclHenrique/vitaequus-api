package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.PropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.PropriedadeSimplesRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDtov2;
import com.reproequinos.vitaequus_api.Dto.Resumo.ProprietarioResumoDTO;
import com.reproequinos.vitaequus_api.entities.Enum.TipoPropriedade;
import com.reproequinos.vitaequus_api.services.PropriedadeService;
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
@RequestMapping("/api/v1/propriedades")
@Tag(name = "Propriedades", description = "Cadastro, consulta e proprietarios vinculados a propriedades")
public class PropriedadeController {

    private final PropriedadeService propriedadeService;

    public PropriedadeController(PropriedadeService propriedadeService) {
        this.propriedadeService = propriedadeService;
    }

    @GetMapping
    @Operation(summary = "Listar propriedades")
    public ResponseEntity<Page<PropriedadeResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) TipoPropriedade tipoPropriedade,
            @RequestParam(required = false) Boolean ativo,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(propriedadeService.listar(nome, cidade, estado, tipoPropriedade, ativo, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar propriedade por ID")
    public ResponseEntity<PropriedadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(propriedadeService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar propriedade")
    public ResponseEntity<PropriedadeResponseDTO> criar(@Valid @RequestBody PropriedadeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propriedadeService.criar(dto));
    }

    @PostMapping("/v2")
    @Operation(summary = "Cadastrar propriedade simples")
    public ResponseEntity<PropriedadeResponseDtov2> criarSimples(@Valid @RequestBody PropriedadeSimplesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propriedadeService.criarSimples(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar propriedade")
    public ResponseEntity<PropriedadeResponseDTO> atualizar(@PathVariable Long id,
                                                            @Valid @RequestBody PropriedadeRequestDTO dto) {
        return ResponseEntity.ok(propriedadeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover propriedade")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        propriedadeService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/proprietarios")
    @Operation(summary = "Listar proprietarios da propriedade")
    public ResponseEntity<List<ProprietarioResumoDTO>> listarProprietarios(@PathVariable Long id) {
        return ResponseEntity.ok(propriedadeService.listarProprietariosDaPropriedade(id));
    }
}
