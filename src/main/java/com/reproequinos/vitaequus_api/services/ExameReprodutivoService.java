package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.ExameReprodutivoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.ExameReprodutivoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.ExameReprodutivoResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.ExameReprodutivo;
import com.reproequinos.vitaequus_api.entities.Insumo;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.AnimalRepository;
import com.reproequinos.vitaequus_api.repositories.ExameReprodutivoRepository;
import com.reproequinos.vitaequus_api.repositories.InsumoRepository;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ExameReprodutivoService {

    private final ExameReprodutivoRepository exameRepository;
    private final AnimalRepository animalRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final InsumoRepository insumoRepository;
    private final AuthService authService;

    public ExameReprodutivoService(
            ExameReprodutivoRepository exameRepository,
            AnimalRepository animalRepository,
            PropriedadeRepository propriedadeRepository,
            InsumoRepository insumoRepository,
            AuthService authService
    ) {
        this.exameRepository = exameRepository;
        this.animalRepository = animalRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.insumoRepository = insumoRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Page<ExameReprodutivoResponseDTO> listar(
            Long animalId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        if (animalId != null) {
            buscarAnimalDoVeterinario(animalId, veterinarioId);
        }
        if (propriedadeId != null) {
            buscarPropriedadeDoVeterinario(propriedadeId, veterinarioId);
        }

        return exameRepository
                .findByFiltros(veterinarioId, animalId, propriedadeId, dataInicio, dataFim, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ExameReprodutivoResponseDTO buscarPorId(Long id) {
        return toResponse(buscarExameDoVeterinario(id));
    }

    @Transactional
    public ExameReprodutivoResponseDTO criar(ExameReprodutivoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();

        validarDiametroFolicular(dto.diametroFolicular());

        Animal animal = buscarAnimalDoVeterinario(dto.animalId(), veterinarioId);
        Propriedade propriedade = buscarPropriedadeDoVeterinario(dto.propriedadeId(), veterinarioId);
        validarAnimalNaPropriedade(animal, propriedade);

        ExameReprodutivo exame = new ExameReprodutivo();
        exame.setAnimal(animal);
        exame.setVeterinario(veterinario);
        exame.setPropriedade(propriedade);
        exame.setDataHora(dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now());
        exame.setDiametroFolicular(dto.diametroFolicular());
        exame.setEdemaUterino(dto.edemaUterino());
        exame.setCorpoLuteo(dto.corpoLuteo());
        exame.setInsumo(buscarInsumoOpcional(dto.insumoId(), veterinarioId));
        exame.setObservacoes(dto.observacoes());

        return toResponse(exameRepository.save(exame));
    }

    @Transactional
    public ExameReprodutivoResponseDTO atualizar(Long id, ExameReprodutivoUpdateDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        ExameReprodutivo exame = buscarExameDoVeterinario(id);

        validarDiametroFolicular(dto.diametroFolicular());

        exame.setDiametroFolicular(dto.diametroFolicular());
        exame.setEdemaUterino(dto.edemaUterino());
        exame.setCorpoLuteo(dto.corpoLuteo());
        exame.setInsumo(buscarInsumoOpcional(dto.insumoId(), veterinarioId));
        exame.setObservacoes(dto.observacoes());

        return toResponse(exame);
    }

    private ExameReprodutivo buscarExameDoVeterinario(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return exameRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Exame reprodutivo nao encontrado"));
    }

    private Animal buscarAnimalDoVeterinario(Long animalId, Long veterinarioId) {
        return animalRepository.findByIdAndPropriedadeVeterinarioId(animalId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Animal nao encontrado"));
    }

    private Propriedade buscarPropriedadeDoVeterinario(Long propriedadeId, Long veterinarioId) {
        return propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(propriedadeId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade nao encontrada"));
    }

    private Insumo buscarInsumoOpcional(Long insumoId, Long veterinarioId) {
        if (insumoId == null) {
            return null;
        }

        return insumoRepository.findByIdAndVeterinarioId(insumoId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Insumo nao encontrado"));
    }

    private void validarAnimalNaPropriedade(Animal animal, Propriedade propriedade) {
        if (!animal.getPropriedade().getId().equals(propriedade.getId())) {
            throw new BadRequestException("Animal nao pertence a propriedade informada");
        }
    }

    private void validarDiametroFolicular(BigDecimal diametroFolicular) {
        if (diametroFolicular != null && diametroFolicular.signum() < 0) {
            throw new BadRequestException("Diametro folicular nao pode ser negativo");
        }
    }

    private ExameReprodutivoResponseDTO toResponse(ExameReprodutivo exame) {
        return new ExameReprodutivoResponseDTO(
                exame.getId(),
                exame.getAnimal().getId(),
                exame.getAnimal().getNome(),
                exame.getPropriedade().getId(),
                exame.getPropriedade().getNome(),
                exame.getVeterinario().getId(),
                exame.getVeterinario().getNome(),
                exame.getDataHora(),
                exame.getDiametroFolicular(),
                exame.getEdemaUterino(),
                exame.getCorpoLuteo(),
                exame.getInsumo() != null ? exame.getInsumo().getId() : null,
                exame.getInsumo() != null ? exame.getInsumo().getNomeComercial() : null,
                exame.getObservacoes()
        );
    }
}
