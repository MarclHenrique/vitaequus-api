package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.AtendimentoClinicoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.AtendimentoClinicoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MedicacaoAplicadaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.AtendimentoClinicoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.MedicacaoAplicadaResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.AtendimentoClinico;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import com.reproequinos.vitaequus_api.entities.Insumo;
import com.reproequinos.vitaequus_api.entities.MedicacaoAplicada;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.AnimalRepository;
import com.reproequinos.vitaequus_api.repositories.AtendimentoClinicoRepository;
import com.reproequinos.vitaequus_api.repositories.InsumoRepository;
import com.reproequinos.vitaequus_api.repositories.MedicacaoAplicadaRepository;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AtendimentoClinicoService {

    private final AtendimentoClinicoRepository atendimentoRepository;
    private final MedicacaoAplicadaRepository medicacaoRepository;
    private final AnimalRepository animalRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final InsumoRepository insumoRepository;
    private final AuthService authService;

    public AtendimentoClinicoService(
            AtendimentoClinicoRepository atendimentoRepository,
            MedicacaoAplicadaRepository medicacaoRepository,
            AnimalRepository animalRepository,
            PropriedadeRepository propriedadeRepository,
            InsumoRepository insumoRepository,
            AuthService authService
    ) {
        this.atendimentoRepository = atendimentoRepository;
        this.medicacaoRepository = medicacaoRepository;
        this.animalRepository = animalRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.insumoRepository = insumoRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Page<AtendimentoClinicoResponseDTO> listar(
            Long animalId,
            TipoAtendimento tipo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        if (animalId != null) {
            buscarAnimalDoVeterinario(animalId, veterinarioId);
        }

        return atendimentoRepository
                .findByFiltros(veterinarioId, animalId, tipo, dataInicio, dataFim, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AtendimentoClinicoResponseDTO buscarPorId(Long id) {
        return toResponse(buscarAtendimentoDoVeterinario(id));
    }

    @Transactional
    public AtendimentoClinicoResponseDTO criar(AtendimentoClinicoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();
        Animal animal = buscarAnimalDoVeterinario(dto.animalId(), veterinarioId);
        Propriedade propriedade = buscarPropriedadeDoVeterinario(dto.propriedadeId(), veterinarioId);

        validarAnimalNaPropriedade(animal, propriedade);

        AtendimentoClinico atendimento = new AtendimentoClinico();
        atendimento.setAnimal(animal);
        atendimento.setVeterinario(veterinario);
        atendimento.setPropriedade(propriedade);
        atendimento.setDataHora(dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now());
        atendimento.setTipoAtendimento(dto.tipoAtendimento());
        atendimento.setQueixaPrincipal(dto.queixaPrincipal());
        atendimento.setDiagnosticoPresuntivo(dto.diagnosticoPresuntivo());
        atendimento.setConduta(dto.conduta());

        if (dto.medicacoes() != null) {
            for (MedicacaoAplicadaRequestDTO medicacaoDto : dto.medicacoes()) {
                atendimento.adicionarMedicacao(criarMedicacao(medicacaoDto, veterinarioId));
            }
        }

        return toResponse(atendimentoRepository.save(atendimento));
    }

    @Transactional
    public AtendimentoClinicoResponseDTO atualizar(Long id, AtendimentoClinicoUpdateDTO dto) {
        AtendimentoClinico atendimento = buscarAtendimentoDoVeterinario(id);

        atendimento.setDiagnosticoPresuntivo(dto.diagnosticoPresuntivo());
        atendimento.setConduta(dto.conduta());

        return toResponse(atendimento);
    }

    @Transactional(readOnly = true)
    public List<MedicacaoAplicadaResponseDTO> listarMedicacoes(Long atendimentoId) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        buscarAtendimentoDoVeterinario(atendimentoId);

        return medicacaoRepository
                .findByAtendimentoIdAndAtendimentoVeterinarioId(atendimentoId, veterinarioId)
                .stream()
                .map(this::toMedicacaoResponse)
                .toList();
    }

    @Transactional
    public MedicacaoAplicadaResponseDTO adicionarMedicacao(Long atendimentoId, MedicacaoAplicadaRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        AtendimentoClinico atendimento = buscarAtendimentoDoVeterinario(atendimentoId);
        MedicacaoAplicada medicacao = criarMedicacao(dto, veterinarioId);

        atendimento.adicionarMedicacao(medicacao);

        return toMedicacaoResponse(medicacaoRepository.save(medicacao));
    }

    @Transactional
    public void removerMedicacao(Long atendimentoId, Long medicacaoId) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        buscarAtendimentoDoVeterinario(atendimentoId);

        MedicacaoAplicada medicacao = medicacaoRepository
                .findByIdAndAtendimentoIdAndAtendimentoVeterinarioId(medicacaoId, atendimentoId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Medicacao nao encontrada"));

        medicacaoRepository.delete(medicacao);
    }

    private AtendimentoClinico buscarAtendimentoDoVeterinario(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return atendimentoRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Atendimento clinico nao encontrado"));
    }

    private Animal buscarAnimalDoVeterinario(Long animalId, Long veterinarioId) {
        return animalRepository.findByIdAndPropriedadeVeterinarioId(animalId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Animal nao encontrado"));
    }

    private Propriedade buscarPropriedadeDoVeterinario(Long propriedadeId, Long veterinarioId) {
        return propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(propriedadeId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade nao encontrada"));
    }

    private Insumo buscarInsumoDoVeterinario(Long insumoId, Long veterinarioId) {
        return insumoRepository.findByIdAndVeterinarioId(insumoId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Insumo nao encontrado"));
    }

    private void validarAnimalNaPropriedade(Animal animal, Propriedade propriedade) {
        if (!animal.getPropriedade().getId().equals(propriedade.getId())) {
            throw new BadRequestException("Animal nao pertence a propriedade informada");
        }
    }

    private MedicacaoAplicada criarMedicacao(MedicacaoAplicadaRequestDTO dto, Long veterinarioId) {
        Insumo insumo = buscarInsumoDoVeterinario(dto.insumoId(), veterinarioId);

        MedicacaoAplicada medicacao = new MedicacaoAplicada();
        medicacao.setInsumo(insumo);
        medicacao.setDose(dto.dose());
        medicacao.setViaAdministracao(dto.viaAdministracao());
        medicacao.setObservacoes(dto.observacoes());
        return medicacao;
    }

    private AtendimentoClinicoResponseDTO toResponse(AtendimentoClinico atendimento) {
        return new AtendimentoClinicoResponseDTO(
                atendimento.getId(),
                atendimento.getAnimal().getId(),
                atendimento.getAnimal().getNome(),
                atendimento.getPropriedade().getId(),
                atendimento.getPropriedade().getNome(),
                atendimento.getVeterinario().getId(),
                atendimento.getVeterinario().getNome(),
                atendimento.getDataHora(),
                atendimento.getTipoAtendimento(),
                atendimento.getQueixaPrincipal(),
                atendimento.getDiagnosticoPresuntivo(),
                atendimento.getConduta(),
                atendimento.getMedicacoes().stream()
                        .map(this::toMedicacaoResponse)
                        .toList()
        );
    }

    private MedicacaoAplicadaResponseDTO toMedicacaoResponse(MedicacaoAplicada medicacao) {
        return new MedicacaoAplicadaResponseDTO(
                medicacao.getId(),
                medicacao.getAtendimento().getId(),
                medicacao.getInsumo().getId(),
                medicacao.getInsumo().getNomeComercial(),
                medicacao.getInsumo().getTipo(),
                medicacao.getDose(),
                medicacao.getViaAdministracao(),
                medicacao.getObservacoes()
        );
    }
}
