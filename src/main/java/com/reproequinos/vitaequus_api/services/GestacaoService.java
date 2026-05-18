package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.CheckupGestacionalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.CheckupGestacionalUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Request.GestacaoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.GestacaoResultadoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CheckupGestacionalResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.GestacaoResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.CheckupGestacional;
import com.reproequinos.vitaequus_api.entities.Cobertura;
import com.reproequinos.vitaequus_api.entities.Doadora;
import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import com.reproequinos.vitaequus_api.entities.Enum.StatusGestacao;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.CheckupGestacionalRepository;
import com.reproequinos.vitaequus_api.repositories.CoberturaRepository;
import com.reproequinos.vitaequus_api.repositories.DoadoraRepository;
import com.reproequinos.vitaequus_api.repositories.GestacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class GestacaoService {

    private final GestacaoRepository gestacaoRepository;
    private final CheckupGestacionalRepository checkupRepository;
    private final CoberturaRepository coberturaRepository;
    private final DoadoraRepository doadoraRepository;
    private final AuthService authService;

    public GestacaoService(
            GestacaoRepository gestacaoRepository,
            CheckupGestacionalRepository checkupRepository,
            CoberturaRepository coberturaRepository,
            DoadoraRepository doadoraRepository,
            AuthService authService
    ) {
        this.gestacaoRepository = gestacaoRepository;
        this.checkupRepository = checkupRepository;
        this.coberturaRepository = coberturaRepository;
        this.doadoraRepository = doadoraRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Page<GestacaoResponseDTO> listar(
            Long doadoraId,
            Long coberturaId,
            ResultadoGestacao resultado,
            StatusGestacao status,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        validarPeriodo(dataInicio, dataFim);

        if (doadoraId != null) {
            buscarDoadoraDoVeterinario(doadoraId, veterinarioId);
        }
        if (coberturaId != null) {
            buscarCoberturaDoVeterinario(coberturaId, veterinarioId);
        }

        return gestacaoRepository
                .findByFiltros(veterinarioId, doadoraId, coberturaId, resultado, status, dataInicio, dataFim, pageable)
                .map(gestacao -> toResponse(gestacao, null));
    }

    @Transactional(readOnly = true)
    public GestacaoResponseDTO buscarPorId(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Gestacao gestacao = buscarGestacaoDoVeterinario(id, veterinarioId);
        return toResponse(gestacao, listarCheckupsEntidades(id, veterinarioId));
    }

    @Transactional
    public GestacaoResponseDTO criar(GestacaoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();

        LocalDate dataDiagnostico = dto.dataDiagnosticoInicial() != null
                ? dto.dataDiagnosticoInicial()
                : LocalDate.now();

        validarDataPrevisaoParto(dataDiagnostico, dto.dataPrevisaoParto());

        Cobertura cobertura = buscarCoberturaDoVeterinario(dto.coberturaId(), veterinarioId);
        Doadora doadora = buscarDoadoraDoVeterinario(dto.doadoraId(), veterinarioId);
        validarDoadoraDaCobertura(doadora, cobertura);

        Gestacao gestacao = new Gestacao();
        gestacao.setDoadora(doadora);
        gestacao.setCobertura(cobertura);
        gestacao.setDataDiagnosticoInicial(dataDiagnostico);
        gestacao.setResultado(dto.resultado());
        gestacao.setStatus(statusPorResultado(dto.resultado()));
        gestacao.setDataPrevisaoParto(dto.dataPrevisaoParto());
        gestacao.setObservacoes(dto.observacoes());

        Gestacao salva = gestacaoRepository.save(gestacao);

        if (dto.resultado() == ResultadoGestacao.PRENHE) {
            criarCheckupsAutomaticosSeNecessario(salva, veterinario, dataDiagnostico);
        }

        return toResponse(salva, listarCheckupsEntidades(salva.getId(), veterinarioId));
    }

    @Transactional
    public GestacaoResponseDTO atualizarResultado(Long id, GestacaoResultadoUpdateDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();
        Gestacao gestacao = buscarGestacaoDoVeterinario(id, veterinarioId);

        gestacao.setResultado(dto.resultado());
        gestacao.setStatus(statusPorResultado(dto.resultado()));
        gestacao.setObservacoes(dto.observacoes());

        if (dto.resultado() == ResultadoGestacao.PRENHE
                && !checkupRepository.existsByGestacaoIdAndGestacaoCoberturaVeterinarioId(id, veterinarioId)) {
            criarCheckupsAutomaticosSeNecessario(gestacao, veterinario, gestacao.getDataDiagnosticoInicial());
        }

        return toResponse(gestacao, listarCheckupsEntidades(id, veterinarioId));
    }

    @Transactional(readOnly = true)
    public List<CheckupGestacionalResponseDTO> listarCheckups(Long gestacaoId) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        buscarGestacaoDoVeterinario(gestacaoId, veterinarioId);

        return listarCheckupsEntidades(gestacaoId, veterinarioId)
                .stream()
                .map(this::toCheckupResponse)
                .toList();
    }

    @Transactional
    public CheckupGestacionalResponseDTO criarCheckup(Long gestacaoId, CheckupGestacionalRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();
        Gestacao gestacao = buscarGestacaoDoVeterinario(gestacaoId, veterinarioId);

        CheckupGestacional checkup = new CheckupGestacional();
        checkup.setGestacao(gestacao);
        checkup.setVeterinario(veterinario);
        checkup.setDataHora(dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now());
        checkup.setResultado(dto.resultado());
        checkup.setObservacoes(dto.observacoes());

        return toCheckupResponse(checkupRepository.save(checkup));
    }

    @Transactional
    public CheckupGestacionalResponseDTO atualizarCheckup(
            Long gestacaoId,
            Long checkupId,
            CheckupGestacionalUpdateDTO dto
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        buscarGestacaoDoVeterinario(gestacaoId, veterinarioId);

        CheckupGestacional checkup = checkupRepository
                .findByIdAndGestacaoIdAndGestacaoCoberturaVeterinarioId(checkupId, gestacaoId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Check-up gestacional nao encontrado"));

        checkup.setResultado(dto.resultado());
        checkup.setObservacoes(dto.observacoes());

        return toCheckupResponse(checkup);
    }

    private Gestacao buscarGestacaoDoVeterinario(Long id, Long veterinarioId) {
        return gestacaoRepository.findByIdAndCoberturaVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Gestacao nao encontrada"));
    }

    private Cobertura buscarCoberturaDoVeterinario(Long coberturaId, Long veterinarioId) {
        return coberturaRepository.findByIdAndVeterinarioId(coberturaId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Cobertura nao encontrada"));
    }

    private Doadora buscarDoadoraDoVeterinario(Long doadoraId, Long veterinarioId) {
        return doadoraRepository.findByIdAndAnimalPropriedadeVeterinarioId(doadoraId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Doadora nao encontrada"));
    }

    private List<CheckupGestacional> listarCheckupsEntidades(Long gestacaoId, Long veterinarioId) {
        return checkupRepository.findByGestacaoIdAndGestacaoCoberturaVeterinarioIdOrderByDataHoraAsc(
                gestacaoId,
                veterinarioId
        );
    }

    private void criarCheckupsAutomaticosSeNecessario(
            Gestacao gestacao,
            Veterinario veterinario,
            LocalDate dataDiagnostico
    ) {
        if (dataDiagnostico == null) {
            dataDiagnostico = LocalDate.now();
        }

        for (int dias : List.of(30, 60, 90)) {
            CheckupGestacional checkup = new CheckupGestacional();
            checkup.setGestacao(gestacao);
            checkup.setVeterinario(veterinario);
            checkup.setDataHora(LocalDateTime.of(dataDiagnostico.plusDays(dias), LocalTime.of(9, 0)));
            checkup.setResultado("AGENDADO");
            checkupRepository.save(checkup);
        }
    }

    private void validarDoadoraDaCobertura(Doadora doadora, Cobertura cobertura) {
        if (!Objects.equals(doadora.getId(), cobertura.getDoadora().getId())) {
            throw new BadRequestException("Doadora informada nao corresponde a doadora da cobertura");
        }
    }

    private void validarDataPrevisaoParto(LocalDate dataDiagnostico, LocalDate dataPrevisaoParto) {
        if (dataPrevisaoParto != null && dataPrevisaoParto.isBefore(dataDiagnostico)) {
            throw new BadRequestException("Data de previsao de parto nao pode ser anterior ao diagnostico");
        }
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data inicial nao pode ser maior que data final");
        }
    }

    private StatusGestacao statusPorResultado(ResultadoGestacao resultado) {
        return resultado == ResultadoGestacao.PRENHE
                ? StatusGestacao.EM_ANDAMENTO
                : StatusGestacao.FINALIZADA;
    }

    private GestacaoResponseDTO toResponse(Gestacao gestacao, List<CheckupGestacional> checkups) {
        Cobertura cobertura = gestacao.getCobertura();
        Doadora doadora = gestacao.getDoadora();

        return new GestacaoResponseDTO(
                gestacao.getId(),
                doadora.getId(),
                doadora.getAnimal().getId(),
                doadora.getAnimal().getNome(),
                cobertura.getId(),
                cobertura.getPropriedade().getId(),
                cobertura.getPropriedade().getNome(),
                cobertura.getVeterinario().getId(),
                cobertura.getVeterinario().getNome(),
                gestacao.getDataDiagnosticoInicial(),
                gestacao.getResultado(),
                gestacao.getStatus(),
                gestacao.getDataPrevisaoParto(),
                gestacao.getObservacoes(),
                checkups != null ? checkups.stream().map(this::toCheckupResponse).toList() : null
        );
    }

    private CheckupGestacionalResponseDTO toCheckupResponse(CheckupGestacional checkup) {
        return new CheckupGestacionalResponseDTO(
                checkup.getId(),
                checkup.getGestacao().getId(),
                checkup.getVeterinario().getId(),
                checkup.getVeterinario().getNome(),
                checkup.getDataHora(),
                checkup.getResultado(),
                checkup.getObservacoes()
        );
    }
}
