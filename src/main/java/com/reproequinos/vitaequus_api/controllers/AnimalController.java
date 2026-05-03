package com.reproequinos.vitaequus_api.controllers;

import com.reproequinos.vitaequus_api.Dto.Request.AnimalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MovimentacaoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.StatusAnimalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.*;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import com.reproequinos.vitaequus_api.services.AnimalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/animais")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public ResponseEntity<Page<AnimalResponseDTO>> listar(
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) StatusAnimal status,
            @RequestParam(required = false) Long idPropriedade,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                animalService.listar(categoria, status, idPropriedade, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDTO> criar(@RequestBody @Valid AnimalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AnimalRequestDTO dto
    ) {
        return ResponseEntity.ok(animalService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AnimalResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid StatusAnimalRequestDTO dto
    ) {
        return ResponseEntity.ok(animalService.atualizarStatus(id, dto.status()));
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<FotoResponseDTO> upload(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(animalService.uploadFoto(id, file));
    }

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<List<MovimentacaoResponseDTO>> listarMovimentacoes(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(animalService.listarMovimentacoes(id));
    }

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<MovimentacaoResponseDTO> registrarMovimentacao(
            @PathVariable Long id,
            @RequestBody @Valid MovimentacaoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(animalService.registrarMovimentacao(id, dto));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<TimelineEventoDTO>> timeline(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false) String tipo
    ) {
        return ResponseEntity.ok(
                animalService.timeline(id, dataInicio, dataFim, tipo)
        );
    }
}