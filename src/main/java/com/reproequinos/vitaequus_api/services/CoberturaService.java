package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.CoberturaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.CoberturaUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CoberturaResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.Cobertura;
import com.reproequinos.vitaequus_api.entities.Doadora;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.TipoProcedimento;
import com.reproequinos.vitaequus_api.entities.Enum.TipoSemen;
import com.reproequinos.vitaequus_api.entities.Produtor;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.CoberturaRepository;
import com.reproequinos.vitaequus_api.repositories.DoadoraRepository;
import com.reproequinos.vitaequus_api.repositories.AnimalRepository;
import com.reproequinos.vitaequus_api.repositories.ProdutorRepository;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CoberturaService {

    private final CoberturaRepository coberturaRepository;
    private final AnimalRepository animalRepository;
    private final DoadoraRepository doadoraRepository;
    private final ProdutorRepository produtorRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final AuthService authService;

    public CoberturaService(
            CoberturaRepository coberturaRepository,
            AnimalRepository animalRepository,
            DoadoraRepository doadoraRepository,
            ProdutorRepository produtorRepository,
            PropriedadeRepository propriedadeRepository,
            AuthService authService
    ) {
        this.coberturaRepository = coberturaRepository;
        this.animalRepository = animalRepository;
        this.doadoraRepository = doadoraRepository;
        this.produtorRepository = produtorRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Page<CoberturaResponseDTO> listar(
            Long doadoraAnimalId,
            Long produtorAnimalId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        validarPeriodo(dataInicio, dataFim);
        if (doadoraAnimalId != null) {
            buscarAnimalDoVeterinario(doadoraAnimalId, veterinarioId);
        }
        if (produtorAnimalId != null) {
            buscarAnimalDoVeterinario(produtorAnimalId, veterinarioId);
        }
        if (propriedadeId != null) {
            buscarPropriedadeDoVeterinario(propriedadeId, veterinarioId);
        }

        return coberturaRepository
                .findByFiltros(veterinarioId, doadoraAnimalId, produtorAnimalId, propriedadeId, dataInicio, dataFim, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CoberturaResponseDTO buscarPorId(Long id) {
        return toResponse(buscarCoberturaDoVeterinario(id));
    }

    @Transactional
    public CoberturaResponseDTO criar(CoberturaRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();

        validarTipoSemen(dto.tipoProcedimento(), dto.tipoSemen());

        Animal animalDoadora = buscarAnimalDoVeterinario(dto.doadoraAnimalId(), veterinarioId);
        Animal animalProdutor = buscarAnimalDoVeterinario(dto.produtorAnimalId(), veterinarioId);
        Propriedade propriedade = buscarPropriedadeDoVeterinario(dto.propriedadeId(), veterinarioId);

        validarAnimalDoadora(animalDoadora);
        validarAnimalProdutor(animalProdutor);
        validarAnimaisDiferentes(animalDoadora, animalProdutor);
        validarAnimalNaPropriedade(animalDoadora, propriedade, "Doadora nao pertence a propriedade informada");
        validarAnimalNaPropriedade(animalProdutor, propriedade, "Produtor nao pertence a propriedade informada");

        Doadora doadora = buscarOuCriarDoadora(animalDoadora);
        Produtor produtor = buscarOuCriarProdutor(animalProdutor);

        Cobertura cobertura = new Cobertura();
        cobertura.setDoadora(doadora);
        cobertura.setProdutor(produtor);
        cobertura.setVeterinario(veterinario);
        cobertura.setPropriedade(propriedade);
        cobertura.setTipoProcedimento(dto.tipoProcedimento());
        cobertura.setTipoSemen(dto.tipoSemen());
        cobertura.setDataHora(dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now());
        cobertura.setObservacoes(dto.observacoes());

        return toResponse(coberturaRepository.save(cobertura));
    }

    @Transactional
    public CoberturaResponseDTO atualizar(Long id, CoberturaUpdateDTO dto) {
        Cobertura cobertura = buscarCoberturaDoVeterinario(id);

        validarTipoSemen(dto.tipoProcedimento(), dto.tipoSemen());

        cobertura.setTipoProcedimento(dto.tipoProcedimento());
        cobertura.setTipoSemen(dto.tipoSemen());
        if (dto.dataHora() != null) {
            cobertura.setDataHora(dto.dataHora());
        }
        cobertura.setObservacoes(dto.observacoes());

        return toResponse(cobertura);
    }

    private Cobertura buscarCoberturaDoVeterinario(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return coberturaRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Cobertura nao encontrada"));
    }

    private Animal buscarAnimalDoVeterinario(Long animalId, Long veterinarioId) {
        return animalRepository.findByIdAndPropriedadeVeterinarioId(animalId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Animal nao encontrado"));
    }

    private Propriedade buscarPropriedadeDoVeterinario(Long propriedadeId, Long veterinarioId) {
        return propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(propriedadeId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade nao encontrada"));
    }

    private void validarAnimalDoadora(Animal animal) {
        Categoria categoria = animal.getCategoria();
        if (categoria != Categoria.EGUA && categoria != Categoria.RECEPTORA) {
            throw new BadRequestException("Doadora deve ser EGUA ou RECEPTORA");
        }
    }

    private void validarAnimalProdutor(Animal animal) {
        if (animal.getCategoria() != Categoria.GARANHAO) {
            throw new BadRequestException("Produtor deve ser GARANHAO");
        }
    }

    private void validarAnimaisDiferentes(Animal doadora, Animal produtor) {
        if (Objects.equals(doadora.getId(), produtor.getId())) {
            throw new BadRequestException("Doadora e produtor nao podem ser o mesmo animal");
        }
    }

    private Doadora buscarOuCriarDoadora(Animal animal) {
        return doadoraRepository
                .findByAnimalId(animal.getId())
                .orElseGet(() -> {
                    Doadora doadora = new Doadora();
                    doadora.setAnimal(animal);
                    return doadoraRepository.save(doadora);
                });
    }

    private Produtor buscarOuCriarProdutor(Animal animal) {
        return produtorRepository
                .findByAnimalId(animal.getId())
                .orElseGet(() -> {
                    Produtor produtor = new Produtor();
                    produtor.setAnimal(animal);
                    return produtorRepository.save(produtor);
                });
    }

    private void validarAnimalNaPropriedade(Animal animal, Propriedade propriedade, String mensagem) {
        if (!Objects.equals(animal.getPropriedade().getId(), propriedade.getId())) {
            throw new BadRequestException(mensagem);
        }
    }

    private void validarTipoSemen(TipoProcedimento tipoProcedimento, TipoSemen tipoSemen) {
        if (tipoProcedimento != TipoProcedimento.MONTA_NATURAL && tipoSemen == null) {
            throw new BadRequestException("Tipo de semen deve ser informado para IA, TE ou ICSI");
        }
    }

    private void validarPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data inicial nao pode ser maior que data final");
        }
    }

    private CoberturaResponseDTO toResponse(Cobertura cobertura) {
        Animal animalDoadora = cobertura.getDoadora().getAnimal();
        Animal animalProdutor = cobertura.getProdutor().getAnimal();

        return new CoberturaResponseDTO(
                cobertura.getId(),
                cobertura.getDoadora().getId(),
                animalDoadora.getId(),
                animalDoadora.getNome(),
                cobertura.getProdutor().getId(),
                animalProdutor.getId(),
                animalProdutor.getNome(),
                cobertura.getPropriedade().getId(),
                cobertura.getPropriedade().getNome(),
                cobertura.getVeterinario().getId(),
                cobertura.getVeterinario().getNome(),
                cobertura.getTipoProcedimento(),
                cobertura.getTipoSemen(),
                cobertura.getDataHora(),
                cobertura.getObservacoes()
        );
    }
}
