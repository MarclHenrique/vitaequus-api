package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.PartoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.PartoUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Request.PotroNascidoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PartoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PotroNascidoResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.Doadora;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.ResultadoGestacao;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import com.reproequinos.vitaequus_api.entities.Parto;
import com.reproequinos.vitaequus_api.entities.PotroNascido;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Proprietario;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.AnimalRepository;
import com.reproequinos.vitaequus_api.repositories.GestacaoRepository;
import com.reproequinos.vitaequus_api.repositories.PartoRepository;
import com.reproequinos.vitaequus_api.repositories.PotroNascidoRepository;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class PartoService {

    private final PartoRepository partoRepository;
    private final PotroNascidoRepository potroNascidoRepository;
    private final GestacaoRepository gestacaoRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final AnimalRepository animalRepository;
    private final AuthService authService;

    public PartoService(
            PartoRepository partoRepository,
            PotroNascidoRepository potroNascidoRepository,
            GestacaoRepository gestacaoRepository,
            PropriedadeRepository propriedadeRepository,
            AnimalRepository animalRepository,
            AuthService authService
    ) {
        this.partoRepository = partoRepository;
        this.potroNascidoRepository = potroNascidoRepository;
        this.gestacaoRepository = gestacaoRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.animalRepository = animalRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Page<PartoResponseDTO> listar(
            Long gestacaoId,
            Long doadoraId,
            Long propriedadeId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        validarPeriodo(dataInicio, dataFim);

        return partoRepository
                .findByFiltros(veterinarioId, gestacaoId, doadoraId, propriedadeId, dataInicio, dataFim, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PartoResponseDTO buscarPorId(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Parto parto = buscarPartoDoVeterinario(id, veterinarioId);
        return toResponse(parto);
    }

    @Transactional
    public PartoResponseDTO criar(PartoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();

        validarCriacao(dto, veterinarioId);

        Gestacao gestacao = gestacaoRepository.findByIdAndCoberturaVeterinarioId(dto.gestacaoId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Gestacao nao encontrada"));

        validarGestacaoPrenhe(gestacao);
        validarPartoDuplicado(dto.gestacaoId(), veterinarioId);

        Propriedade propriedade = propriedadeRepository
                .findByIdAndAtivoTrueAndVeterinarioId(dto.propriedadeId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade nao encontrada"));

        validarPropriedadeDaGestacao(propriedade, gestacao);

        LocalDateTime dataHora = dto.dataHora() != null ? dto.dataHora() : LocalDateTime.now();
        Doadora doadora = gestacao.getDoadora();

        Parto parto = new Parto();
        parto.setGestacao(gestacao);
        parto.setDoadora(doadora);
        parto.setVeterinario(veterinario);
        parto.setPropriedade(propriedade);
        parto.setDataHora(dataHora);
        parto.setTipoParto(dto.tipoParto());
        parto.setResultado(dto.resultadoParto());
        parto.setIntercorrencias(dto.intercorrencias());
        parto.setObservacoes(dto.observacoes());

        Parto partoSalvo = partoRepository.save(parto);

        for (PotroNascidoRequestDTO potroDto : potrosInformados(dto.potros())) {
            criarPotro(partoSalvo, potroDto);
        }

        return toResponse(buscarPartoDoVeterinario(partoSalvo.getId(), veterinarioId));
    }

    @Transactional
    public PartoResponseDTO atualizar(Long id, PartoUpdateDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Parto parto = buscarPartoDoVeterinario(id, veterinarioId);

        parto.setTipoParto(dto.tipoParto());
        parto.setResultado(dto.resultadoParto());
        parto.setIntercorrencias(dto.intercorrencias());
        parto.setObservacoes(dto.observacoes());

        return toResponse(parto);
    }

    @Transactional(readOnly = true)
    public List<PotroNascidoResponseDTO> listarPotros(Long partoId) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        buscarPartoDoVeterinario(partoId, veterinarioId);

        return potroNascidoRepository.findByPartoIdAndPartoVeterinarioId(partoId, veterinarioId)
                .stream()
                .map(this::toPotroResponse)
                .toList();
    }

    @Transactional
    public PotroNascidoResponseDTO adicionarPotro(Long partoId, PotroNascidoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Parto parto = buscarPartoDoVeterinario(partoId, veterinarioId);

        validarPotro(dto);

        PotroNascido potro = criarPotro(parto, dto);
        return toPotroResponse(potro);
    }

    @Transactional
    public PotroNascidoResponseDTO atualizarPotro(Long partoId, Long potroId, PotroNascidoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        buscarPartoDoVeterinario(partoId, veterinarioId);

        validarPotro(dto);

        PotroNascido potro = potroNascidoRepository
                .findByIdAndPartoIdAndPartoVeterinarioId(potroId, partoId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Potro nao encontrado"));

        atualizarDadosPotro(potro, dto);

        Animal animal = potro.getAnimalCriado();
        if (animal == null) {
            animal = criarAnimalDoPotro(dto, potro.getParto(), potro.getParto().getPropriedade(), potro.getParto().getDoadora());
            potro.setAnimalCriado(animal);
        } else {
            atualizarAnimalDoPotro(animal, dto);
        }

        return toPotroResponse(potro);
    }

    private PotroNascido criarPotro(Parto parto, PotroNascidoRequestDTO dto) {
        validarPotro(dto);

        Animal animal = criarAnimalDoPotro(dto, parto, parto.getPropriedade(), parto.getDoadora());

        PotroNascido potro = new PotroNascido();
        potro.setParto(parto);
        potro.setAnimalCriado(animal);
        atualizarDadosPotro(potro, dto);

        return potroNascidoRepository.save(potro);
    }

    private Animal criarAnimalDoPotro(
            PotroNascidoRequestDTO potroDto,
            Parto parto,
            Propriedade propriedade,
            Doadora doadora
    ) {
        Animal animal = new Animal();
        animal.setNome(potroDto.nome());
        animal.setIdentificacao(potroDto.identificacao());
        animal.setCategoria(Categoria.POTRO);
        animal.setSexo(potroDto.sexo());
        animal.setDataNascimento(parto.getDataHora().toLocalDate());
        animal.setPelagem(potroDto.pelagemInicial());
        animal.setPropriedade(propriedade);
        animal.setProprietario(proprietarioDaDoadora(doadora));
        animal.setStatus(StatusAnimal.ATIVO);
        animal.setBiografia("Animal criado automaticamente a partir do parto " + parto.getId());
        return animalRepository.save(animal);
    }

    private void atualizarAnimalDoPotro(Animal animal, PotroNascidoRequestDTO potroDto) {
        animal.setNome(potroDto.nome());
        animal.setIdentificacao(potroDto.identificacao());
        animal.setSexo(potroDto.sexo());
        animal.setPelagem(potroDto.pelagemInicial());
    }

    private void atualizarDadosPotro(PotroNascido potro, PotroNascidoRequestDTO potroDto) {
        potro.setSexo(potroDto.sexo());
        potro.setPelagemInicial(potroDto.pelagemInicial());
        potro.setPesoNascimento(potroDto.pesoNascimento());
        potro.setResultado(potroDto.resultado());
        potro.setCondicaoNeonato(potroDto.condicaoNeonato());
        potro.setObservacoes(potroDto.observacoes());
    }

    private Proprietario proprietarioDaDoadora(Doadora doadora) {
        if (doadora == null || doadora.getAnimal() == null) {
            return null;
        }
        return doadora.getAnimal().getProprietario();
    }

    private void validarCriacao(PartoRequestDTO dto, Long veterinarioId) {
        for (PotroNascidoRequestDTO potro : potrosInformados(dto.potros())) {
            validarPotro(potro);
        }

        if (dto.gestacaoId() != null) {
            gestacaoRepository.findByIdAndCoberturaVeterinarioId(dto.gestacaoId(), veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Gestacao nao encontrada"));
        }
        if (dto.propriedadeId() != null) {
            propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(dto.propriedadeId(), veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Propriedade nao encontrada"));
        }
    }

    private void validarPotro(PotroNascidoRequestDTO potro) {
        if (potro.sexo() == null) {
            throw new BadRequestException("Sexo do potro e obrigatorio");
        }
        if (potro.resultado() == null) {
            throw new BadRequestException("Resultado do potro e obrigatorio");
        }
        if (potro.pesoNascimento() != null && potro.pesoNascimento().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Peso de nascimento nao pode ser negativo");
        }
    }

    private List<PotroNascidoRequestDTO> potrosInformados(List<PotroNascidoRequestDTO> potros) {
        return potros == null ? List.of() : potros;
    }

    private void validarGestacaoPrenhe(Gestacao gestacao) {
        if (gestacao.getResultado() != ResultadoGestacao.PRENHE) {
            throw new BadRequestException("Parto so pode ser registrado para gestacao PRENHE");
        }
    }

    private void validarPartoDuplicado(Long gestacaoId, Long veterinarioId) {
        if (partoRepository.existsByGestacaoIdAndVeterinarioId(gestacaoId, veterinarioId)) {
            throw new BadRequestException("Ja existe parto registrado para esta gestacao");
        }
    }

    private void validarPropriedadeDaGestacao(Propriedade propriedade, Gestacao gestacao) {
        if (gestacao.getCobertura() != null
                && gestacao.getCobertura().getPropriedade() != null
                && !Objects.equals(propriedade.getId(), gestacao.getCobertura().getPropriedade().getId())) {
            throw new BadRequestException("Propriedade informada nao corresponde a propriedade da gestacao");
        }
    }

    private void validarPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data inicial nao pode ser maior que data final");
        }
    }

    private Parto buscarPartoDoVeterinario(Long id, Long veterinarioId) {
        return partoRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Parto nao encontrado"));
    }

    private PartoResponseDTO toResponse(Parto parto) {
        List<PotroNascidoResponseDTO> potros = parto.getPotros() == null
                ? List.of()
                : parto.getPotros().stream()
                        .sorted(Comparator.comparing(PotroNascido::getId, Comparator.nullsLast(Long::compareTo)))
                        .map(this::toPotroResponse)
                        .toList();

        return new PartoResponseDTO(
                parto.getId(),
                parto.getGestacao().getId(),
                parto.getDoadora().getId(),
                parto.getDoadora().getAnimal().getId(),
                parto.getDoadora().getAnimal().getNome(),
                parto.getPropriedade().getId(),
                parto.getPropriedade().getNome(),
                parto.getVeterinario().getId(),
                parto.getVeterinario().getNome(),
                parto.getDataHora(),
                parto.getTipoParto(),
                parto.getResultado(),
                parto.getIntercorrencias(),
                parto.getObservacoes(),
                potros
        );
    }

    private PotroNascidoResponseDTO toPotroResponse(PotroNascido potro) {
        Animal animal = potro.getAnimalCriado();

        return new PotroNascidoResponseDTO(
                potro.getId(),
                potro.getParto().getId(),
                animal != null ? animal.getId() : null,
                animal != null ? animal.getNome() : null,
                animal != null ? animal.getIdentificacao() : null,
                potro.getSexo(),
                potro.getPelagemInicial(),
                potro.getPesoNascimento(),
                potro.getResultado(),
                potro.getCondicaoNeonato(),
                potro.getObservacoes()
        );
    }
}
