package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.AnimalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MovimentacaoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.*;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.*;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.Sexo;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final RacaRepository racaRepository;
    private final CuidadorPropriedadeRepository cuidadorRepository;
    private final MovimentacaoAnimalRepository movimentacaoRepository;
    private final AtendimentoClinicoRepository atendimentoClinicoRepository;
    private final ExameReprodutivoRepository exameReprodutivoRepository;
    private final CoberturaRepository coberturaRepository;
    private final GestacaoRepository gestacaoRepository;
    private final CheckupGestacionalRepository checkupGestacionalRepository;
    private final AuthService authService;

    @Value("${app.upload.animais-dir:uploads/animais}")
    private String animaisUploadDir;

    public AnimalService(
            AnimalRepository animalRepository,
            PropriedadeRepository propriedadeRepository,
            ProprietarioRepository proprietarioRepository,
            RacaRepository racaRepository,
            CuidadorPropriedadeRepository cuidadorRepository,
            MovimentacaoAnimalRepository movimentacaoRepository,
            AtendimentoClinicoRepository atendimentoClinicoRepository,
            ExameReprodutivoRepository exameReprodutivoRepository,
            CoberturaRepository coberturaRepository,
            GestacaoRepository gestacaoRepository,
            CheckupGestacionalRepository checkupGestacionalRepository,
            AuthService authService
    ) {
        this.animalRepository = animalRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.proprietarioRepository = proprietarioRepository;
        this.racaRepository = racaRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.atendimentoClinicoRepository = atendimentoClinicoRepository;
        this.exameReprodutivoRepository = exameReprodutivoRepository;
        this.coberturaRepository = coberturaRepository;
        this.gestacaoRepository = gestacaoRepository;
        this.checkupGestacionalRepository = checkupGestacionalRepository;
        this.authService = authService;
    }

    public Page<AnimalResponseDTO> listar(
            Categoria categoria,
            StatusAnimal status,
            Long idPropriedade,
            Pageable pageable
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        Page<Animal> page;

        if (categoria != null) {
            page = animalRepository.findByCategoriaAndPropriedadeVeterinarioId(
                    categoria, veterinarioId, pageable
            );
        } else if (status != null) {
            page = animalRepository.findByStatusAndPropriedadeVeterinarioId(
                    status, veterinarioId, pageable
            );
        } else if (idPropriedade != null) {
            page = animalRepository.findByPropriedadeIdAndPropriedadeVeterinarioId(
                    idPropriedade, veterinarioId, pageable
            );
        } else {
            page = animalRepository.findByPropriedadeVeterinarioId(
                    veterinarioId, pageable
            );
        }

        return page.map(this::toResponse);
    }

    @Transactional
    public AnimalResponseDTO criar(AnimalRequestDTO dto) {

        Veterinario vet = authService.getVeterinarioLogado();

        Long veterinarioId = authService.getVeterinarioLogadoId();

        Propriedade prop = propriedadeRepository
                .findByIdAndAtivoTrueAndVeterinarioId(dto.propriedadeId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade não encontrada ou sem acesso"));

        validarCategoriaSexo(dto.categoria(), dto.sexo());

        Animal animal = new Animal();
        // identificacao
        animal.setIdentificacao(dto.identificacao());
        animal.setNome(dto.nome());
        animal.setCategoria(dto.categoria());
        animal.setSexo(dto.sexo());
        animal.setDataNascimento(dto.dataNascimento());
        animal.setPelagem(dto.pelagem());
        animal.setPropriedade(prop);
        animal.setStatus(dto.status() != null ? dto.status() : StatusAnimal.ATIVO);
        animal.setBiografia(dto.biografia());

        // 🔹 RAÇA (você já tem, mas mantendo aqui organizado)
        if (dto.racaId() != null) {
            Raca raca = racaRepository.findByIdAndStatus(dto.racaId(), 0)
                    .orElseThrow(() -> new NotFoundException("Raça não encontrada"));
            animal.setRaca(raca);
        }

// 🔹 PROPRIETÁRIO
        if (dto.proprietarioId() != null) {
            Proprietario proprietario = proprietarioRepository
                    .findByIdAndVeterinarioId(dto.proprietarioId(), veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Proprietário não encontrado"));

            animal.setProprietario(proprietario);
        }

// 🔹 CUIDADOR
        if (dto.cuidadorPropriedadeId() != null) {
            CuidadorPropriedade cuidador = cuidadorRepository
                    .findByIdAndPropriedadeVeterinarioId(dto.cuidadorPropriedadeId(), veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Cuidador não encontrado"));

            animal.setCuidadorPropriedade(cuidador);
        }

        Animal salvo = animalRepository.save(animal);

        MovimentacaoAnimal mov = new MovimentacaoAnimal();
        mov.setAnimal(salvo);
        mov.setPropriedade(prop);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setMotivo("Cadastro inicial por " + vet.getNome());

        movimentacaoRepository.save(mov);

        return toResponse(salvo);
    }

    @Transactional
    public AnimalResponseDTO atualizar(Long id, AnimalRequestDTO dto) {
        Animal animal = buscarAnimal(id);

        validarCategoriaSexo(dto.categoria(), dto.sexo());

        animal.setIdentificacao(dto.identificacao());
        animal.setNome(dto.nome());
        animal.setCategoria(dto.categoria());
        animal.setSexo(dto.sexo());
        animal.setDataNascimento(dto.dataNascimento());
        animal.setPelagem(dto.pelagem());
        animal.setBiografia(dto.biografia());

        if (dto.status() != null) {
            animal.setStatus(dto.status());
        }

        if (dto.propriedadeId() != null) {
            Long veterinarioId = authService.getVeterinarioLogadoId();
            Propriedade propriedade = propriedadeRepository
                    .findByIdAndAtivoTrueAndVeterinarioId(dto.propriedadeId(), veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Propriedade não encontrada"));
            animal.setPropriedade(propriedade);
        }

        if (dto.racaId() != null) {
            Raca raca = racaRepository.findByIdAndStatus(dto.racaId(), 0)
                    .orElseThrow(() -> new NotFoundException("Raça não encontrada"));
            animal.setRaca(raca);
        }

        if (dto.proprietarioId() != null) {
            Long veterinarioId = authService.getVeterinarioLogadoId();
            Proprietario proprietario = proprietarioRepository
                    .findByIdAndVeterinarioId(dto.proprietarioId(), veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Proprietário não encontrado"));
            animal.setProprietario(proprietario);
        }

        if (dto.cuidadorPropriedadeId() != null) {
            Long veterinarioId = authService.getVeterinarioLogadoId();
            CuidadorPropriedade cuidador = cuidadorRepository
                    .findByIdAndPropriedadeVeterinarioId(dto.cuidadorPropriedadeId(), veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Cuidador da propriedade não encontrado"));
            animal.setCuidadorPropriedade(cuidador);
        }

        return toResponse(animal);
    }

    @Transactional
    public FotoResponseDTO uploadFoto(Long id, MultipartFile file) {
        Animal animal = buscarAnimal(id);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Arquivo de imagem é obrigatório");
        }

        String extensao = extrairExtensao(file.getOriginalFilename());
        if (!Set.of("jpg", "jpeg", "png", "webp").contains(extensao)) {
            throw new BadRequestException("Formato de imagem inválido");
        }

        try {
            Path pasta = Paths.get(animaisUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(pasta);

            String nome = "animal_" + id + "_" + UUID.randomUUID() + "." + extensao;
            Path path = pasta.resolve(nome).normalize();

            if (!path.startsWith(pasta)) {
                throw new BadRequestException("Nome de arquivo inválido");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }

            String url = "/uploads/animais/" + nome;

            animal.setUrlFoto(url);

            return new FotoResponseDTO(url);

        } catch (Exception e) {
            if (e instanceof BadRequestException badRequestException) {
                throw badRequestException;
            }
            throw new BadRequestException("Erro ao salvar imagem");
        }
    }

    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new BadRequestException("Nome de arquivo inválido");
        }

        String nomeNormalizado = Paths.get(nomeArquivo).getFileName().toString();
        int index = nomeNormalizado.lastIndexOf('.');

        if (index < 0 || index == nomeNormalizado.length() - 1) {
            throw new BadRequestException("Arquivo sem extensão");
        }

        return nomeNormalizado.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    @Transactional
    public AnimalResponseDTO atualizarStatus(Long id, StatusAnimal status) {
        Animal animal = buscarAnimal(id);
        animal.setStatus(status);
        return toResponse(animal);
    }

    @Transactional(readOnly = true)
    public List<TimelineEventoDTO> timeline(
            Long id,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String tipo
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        buscarAnimal(id);

        List<TimelineEventoDTO> lista = new ArrayList<>();

        List<MovimentacaoAnimal> movs = movimentacaoRepository
                .findByAnimalIdOrderByDataMovimentacaoDesc(id);

        for (MovimentacaoAnimal m : movs) {
            lista.add(new TimelineEventoDTO(
                    m.getId(),
                    "MOVIMENTACAO",
                    "Movimentação",
                    m.getMotivo(),
                    m.getDataMovimentacao()
            ));
        }

        List<AtendimentoClinico> atendimentos = atendimentoClinicoRepository
                .findTimelineByAnimalAndVeterinario(id, veterinarioId, dataInicio, dataFim);

        for (AtendimentoClinico atendimento : atendimentos) {
            lista.add(new TimelineEventoDTO(
                    atendimento.getId(),
                    "ATENDIMENTO_CLINICO",
                    "Atendimento clinico",
                    atendimento.getTipoAtendimento().name(),
                    atendimento.getDataHora()
            ));
        }

        List<ExameReprodutivo> exames = exameReprodutivoRepository
                .findTimelineByAnimalAndVeterinario(id, veterinarioId, dataInicio, dataFim);

        for (ExameReprodutivo exame : exames) {
            lista.add(new TimelineEventoDTO(
                    exame.getId(),
                    "EXAME_REPRODUTIVO",
                    "Exame reprodutivo",
                    descricaoExameReprodutivo(exame),
                    exame.getDataHora()
            ));
        }

        List<Cobertura> coberturas = coberturaRepository
                .findTimelineByAnimalAndVeterinario(id, veterinarioId, dataInicio, dataFim);

        for (Cobertura cobertura : coberturas) {
            lista.add(new TimelineEventoDTO(
                    cobertura.getId(),
                    "COBERTURA",
                    "Cobertura",
                    descricaoCobertura(cobertura, id),
                    cobertura.getDataHora()
            ));
        }

        List<Gestacao> gestacoes = gestacaoRepository
                .findTimelineByDoadoraAnimalAndVeterinario(
                        id,
                        veterinarioId,
                        dataInicio,
                        dataFim,
                        dataInicio != null ? dataInicio.toLocalDate() : null,
                        dataFim != null ? dataFim.toLocalDate() : null
                );

        for (Gestacao gestacao : gestacoes) {
            lista.add(new TimelineEventoDTO(
                    gestacao.getId(),
                    "GESTACAO",
                    "Diagnostico de gestacao",
                    descricaoGestacao(gestacao),
                    gestacao.getDataDiagnosticoInicial().atStartOfDay()
            ));
        }

        List<CheckupGestacional> checkups = checkupGestacionalRepository
                .findTimelineByDoadoraAnimalAndVeterinario(id, veterinarioId, dataInicio, dataFim);

        for (CheckupGestacional checkup : checkups) {
            lista.add(new TimelineEventoDTO(
                    checkup.getId(),
                    "CHECKUP_GESTACIONAL",
                    "Check-up gestacional",
                    descricaoCheckupGestacional(checkup),
                    checkup.getDataHora()
            ));
        }

        return lista.stream()
                .filter(e -> dataInicio == null || !e.dataHora().isBefore(dataInicio))
                .filter(e -> dataFim == null || !e.dataHora().isAfter(dataFim))
                .filter(e -> tipo == null || e.tipo().equalsIgnoreCase(tipo))
                .sorted(Comparator.comparing(TimelineEventoDTO::dataHora).reversed())
                .toList();
    }

    private String descricaoExameReprodutivo(ExameReprodutivo exame) {
        List<String> partes = new ArrayList<>();

        if (exame.getDiametroFolicular() != null) {
            partes.add("Diametro folicular: " + exame.getDiametroFolicular());
        }
        if (exame.getEdemaUterino() != null) {
            partes.add("Edema uterino: " + exame.getEdemaUterino().name());
        }
        if (exame.getCorpoLuteo() != null) {
            partes.add("Corpo luteo: " + exame.getCorpoLuteo().name());
        }

        return String.join("; ", partes);
    }

    private String descricaoGestacao(Gestacao gestacao) {
        List<String> partes = new ArrayList<>();
        partes.add("Resultado: " + gestacao.getResultado().name());

        if (gestacao.getDataPrevisaoParto() != null) {
            partes.add("Previsao de parto: " + gestacao.getDataPrevisaoParto());
        }
        if (gestacao.getObservacoes() != null && !gestacao.getObservacoes().isBlank()) {
            partes.add(gestacao.getObservacoes());
        }

        return String.join("; ", partes);
    }

    private String descricaoCheckupGestacional(CheckupGestacional checkup) {
        List<String> partes = new ArrayList<>();

        if (checkup.getResultado() != null && !checkup.getResultado().isBlank()) {
            partes.add(checkup.getResultado());
        }
        if (checkup.getObservacoes() != null && !checkup.getObservacoes().isBlank()) {
            partes.add(checkup.getObservacoes());
        }

        return String.join("; ", partes);
    }

    private String descricaoCobertura(Cobertura cobertura, Long animalId) {
        List<String> partes = new ArrayList<>();
        partes.add(cobertura.getTipoProcedimento().name());

        if (cobertura.getTipoSemen() != null) {
            partes.add("Semen: " + cobertura.getTipoSemen().name());
        }

        if (Objects.equals(cobertura.getDoadora().getAnimal().getId(), animalId)) {
            partes.add("Produtor: " + cobertura.getProdutor().getAnimal().getNome());
        } else {
            partes.add("Doadora: " + cobertura.getDoadora().getAnimal().getNome());
        }

        return String.join("; ", partes);
    }

    @Transactional
    public MovimentacaoResponseDTO registrarMovimentacao(Long animalId, MovimentacaoRequestDTO dto) {
        Veterinario vet = authService.getVeterinarioLogado();

        Animal animal = buscarAnimal(animalId);

        Long veterinarioId = authService.getVeterinarioLogadoId();

        Propriedade novaPropriedade = propriedadeRepository
                .findByIdAndAtivoTrueAndVeterinarioId(dto.propriedadeId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade não encontrada"));

        animal.setPropriedade(novaPropriedade);

        MovimentacaoAnimal mov = new MovimentacaoAnimal();
        mov.setAnimal(animal);
        mov.setPropriedade(novaPropriedade);
        mov.setDataMovimentacao(
                dto.dataMovimentacao() != null ? dto.dataMovimentacao() : LocalDateTime.now()
        );
        mov.setMotivo(
                dto.motivo() != null ? dto.motivo() : "Movimentação registrada por " + vet.getNome()
        );

        MovimentacaoAnimal salvo = movimentacaoRepository.save(mov);

        return toMovimentacaoResponse(salvo);
    }

    public List<MovimentacaoResponseDTO> listarMovimentacoes(Long animalId) {
        buscarAnimal(animalId);

        return movimentacaoRepository
                .findByAnimalIdOrderByDataMovimentacaoDesc(animalId)
                .stream()
                .map(this::toMovimentacaoResponse)
                .toList();
    }

    private Animal buscarAnimal(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return animalRepository.findByIdAndPropriedadeVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Animal não encontrado ou sem acesso"));
    }

    public AnimalResponseDTO buscarPorId(Long id) {
        Animal animal = buscarAnimal(id); // já valida veterinário

        return toResponse(animal);
    }

    private void validarCategoriaSexo(Categoria categoria, Sexo sexo) {
        if (categoria == Categoria.GARANHAO && sexo != Sexo.M) {
            throw new BadRequestException("Garanhão deve ser macho");
        }
    }

    private AnimalResponseDTO toResponse(Animal a) {
        return new AnimalResponseDTO(
                a.getId(),
                a.getIdentificacao(),
                a.getNome(),
                a.getCategoria(),
                a.getSexo(),
                a.getDataNascimento(),
                a.getRaca() != null ? a.getRaca().getId() : null,
                a.getRaca() != null ? a.getRaca().getNome() : null,
                a.getPelagem(),
                a.getPropriedade().getId(),
                a.getPropriedade().getNome(),
                a.getProprietario() != null ? a.getProprietario().getId() : null,
                a.getProprietario() != null ? a.getProprietario().getNome() : null,
                a.getStatus(),
                a.getBiografia(),
                a.getUrlFoto()
        );
    }

    private MovimentacaoResponseDTO toMovimentacaoResponse(MovimentacaoAnimal m) {
        return new MovimentacaoResponseDTO(
                m.getId(),
                m.getAnimal().getId(),
                m.getAnimal().getNome(),
                m.getPropriedade().getId(),
                m.getPropriedade().getNome(),
                m.getDataMovimentacao(),
                m.getMotivo()
        );
    }
}
