package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.AnimalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MovimentacaoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.StatusAnimalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.*;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import com.reproequinos.vitaequus_api.services.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/animais")
@Tag(name = "Animais", description = "Cadastro, movimentacao, timeline e fotos dos animais")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    @Operation(summary = "Listar animais", description = "Lista animais do veterinario logado com filtros opcionais e paginacao.")
    public ResponseEntity<Page<AnimalResponseDTO>> listar(
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) StatusAnimal status,
            @RequestParam(required = false) Long idPropriedade,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                animalService.listar(categoria, status, idPropriedade, pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar animal por ID")
    public ResponseEntity<AnimalResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar animal")
    @ApiResponse(responseCode = "201", description = "Animal cadastrado")
    public ResponseEntity<AnimalResponseDTO> criar(@RequestBody @Valid AnimalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar animal")
    public ResponseEntity<AnimalResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AnimalRequestDTO dto
    ) {
        return ResponseEntity.ok(animalService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do animal")
    public ResponseEntity<AnimalResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid StatusAnimalRequestDTO dto
    ) {
        return ResponseEntity.ok(animalService.atualizarStatus(id, dto.status()));
    }

    @Operation(
            summary = "Enviar foto do animal",
            description = "Recebe uma imagem jpg, jpeg, png ou webp em multipart/form-data e retorna a URL publica da foto.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "object", requiredProperties = "file"),
                            schemaProperties = @SchemaProperty(
                                    name = "file",
                                    schema = @Schema(type = "string", format = "binary", description = "Arquivo da foto")
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto salva"),
            @ApiResponse(responseCode = "400", description = "Arquivo invalido"),
            @ApiResponse(responseCode = "404", description = "Animal nao encontrado ou sem acesso")
    })
    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FotoResponseDTO> upload(
            @PathVariable Long id,
            @Parameter(description = "Arquivo da foto", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(animalService.uploadFoto(id, file));
    }

    @GetMapping("/{id}/movimentacoes")
    @Operation(summary = "Listar movimentacoes do animal")
    public ResponseEntity<List<MovimentacaoResponseDTO>> listarMovimentacoes(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(animalService.listarMovimentacoes(id));
    }

    @PostMapping("/{id}/movimentacoes")
    @Operation(summary = "Registrar movimentacao do animal")
    public ResponseEntity<MovimentacaoResponseDTO> registrarMovimentacao(
            @PathVariable Long id,
            @RequestBody @Valid MovimentacaoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(animalService.registrarMovimentacao(id, dto));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Consultar timeline do animal")
    public ResponseEntity<List<TimelineEventoDTO>> timeline(
            @PathVariable Long id,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false) String tipo
    ) {
        return ResponseEntity.ok(
                animalService.timeline(id, dataInicio, dataFim, tipo)
        );
    }
}
