package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.AtendimentoClinicoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MedicacaoAplicadaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.AtendimentoClinicoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.MedicacaoAplicadaResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.*;
import com.reproequinos.vitaequus_api.entities.Enum.TipoAtendimento;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.*;
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
            AuthService authService) {
        this.atendimentoRepository = atendimentoRepository;
        this.medicacaoRepository = medicacaoRepository;
        this.animalRepository = animalRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.insumoRepository = insumoRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Page<AtendimentoClinicoResponseDTO> listarAtendimentos(
            Long animalId, TipoAtendimento tipo, LocalDateTime dataInicio,
            LocalDateTime dataFim, Pageable pageable) {

        Long veterinarioId = authService.getVeterinarioLogadoId();

        Page<AtendimentoClinico> atendimentos = atendimentoRepository.findComFiltros(
                veterinarioId, animalId, tipo, dataInicio, dataFim, pageable
        );

        return atendimentos.map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public AtendimentoClinicoResponseDTO detalharAtendimento(Long id) {
        AtendimentoClinico atendimento = buscarAtendimentoPorId(id);
        return toResponseDTOComMedicacoes(atendimento);
    }

    @Transactional
    public AtendimentoClinicoResponseDTO registrarAtendimento(AtendimentoClinicoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();

        Animal animal = animalRepository.findByIdAndPropriedadeVeterinarioId(dto.animalId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Animal não encontrado"));

        Propriedade propriedade = propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(dto.propriedadeId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade não encontrada"));

        AtendimentoClinico atendimento = new AtendimentoClinico();
        atendimento.setAnimal(animal);
        atendimento.setVeterinario(veterinario);
        atendimento.setPropriedade(propriedade);
        atendimento.setDataHora(dto.dataHora());
        atendimento.setTipoAtendimento(dto.tipoAtendimento());
        atendimento.setQueixaPrincipal(dto.queixaPrincipal());
        atendimento.setDiagnosticoPresuntivo(dto.diagnosticoPresuntivo());
        atendimento.setConduta(dto.conduta());

        atendimento = atendimentoRepository.save(atendimento);

        return toResponseDTO(atendimento);
    }

    @Transactional
    public AtendimentoClinicoResponseDTO atualizarAtendimento(Long id, AtendimentoClinicoRequestDTO dto) {
        AtendimentoClinico atendimento = buscarAtendimentoPorId(id);

        atendimento.setDiagnosticoPresuntivo(dto.diagnosticoPresuntivo());
        atendimento.setConduta(dto.conduta());
        atendimento.setQueixaPrincipal(dto.queixaPrincipal());

        return toResponseDTO(atendimento);
    }

    @Transactional(readOnly = true)
    public List<MedicacaoAplicadaResponseDTO> listarMedicacoes(Long atendimentoId) {
        buscarAtendimentoPorId(atendimentoId);

        return medicacaoRepository.findByAtendimentoId(atendimentoId)
                .stream()
                .map(this::toMedicacaoResponseDTO)
                .toList();
    }

    @Transactional
    public MedicacaoAplicadaResponseDTO adicionarMedicacao(Long atendimentoId, MedicacaoAplicadaRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        AtendimentoClinico atendimento = buscarAtendimentoPorId(atendimentoId);

        Insumo insumo = insumoRepository.findByIdAndVeterinarioId(dto.insumoId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Insumo não encontrado"));

        MedicacaoAplicada medicacao = new MedicacaoAplicada();
        medicacao.setAtendimento(atendimento);
        medicacao.setInsumo(insumo);
        medicacao.setDose(dto.dose());
        medicacao.setViaAdministracao(dto.viaAdministracao());
        medicacao.setObservacoes(dto.observacoes());

        medicacao = medicacaoRepository.save(medicacao);

        return toMedicacaoResponseDTO(medicacao);
    }

    @Transactional
    public void removerMedicacao(Long atendimentoId, Long medicacaoId) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        buscarAtendimentoPorId(atendimentoId);

        MedicacaoAplicada medicacao = medicacaoRepository.findByIdAndAtendimentoVeterinarioId(medicacaoId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Medicação não encontrada"));

        medicacaoRepository.delete(medicacao);
    }

    private AtendimentoClinico buscarAtendimentoPorId(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        return atendimentoRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado"));
    }

    private AtendimentoClinicoResponseDTO toResponseDTO(AtendimentoClinico atendimento) {
        return new AtendimentoClinicoResponseDTO(
                atendimento.getId(),
                atendimento.getAnimal().getId(),
                atendimento.getAnimal().getNome(),
                atendimento.getVeterinario().getId(),
                atendimento.getVeterinario().getNome(),
                atendimento.getPropriedade().getId(),
                atendimento.getPropriedade().getNome(),
                atendimento.getDataHora(),
                atendimento.getTipoAtendimento(),
                atendimento.getQueixaPrincipal(),
                atendimento.getDiagnosticoPresuntivo(),
                atendimento.getConduta(),
                null
        );
    }

    private AtendimentoClinicoResponseDTO toResponseDTOComMedicacoes(AtendimentoClinico atendimento) {
        List<MedicacaoAplicadaResponseDTO> medicacoes = medicacaoRepository
                .findByAtendimentoId(atendimento.getId())
                .stream()
                .map(this::toMedicacaoResponseDTO)
                .toList(