package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.AnimalRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.MovimentacaoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.*;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.*;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.Sexo;
import com.reproequinos.vitaequus_api.entities.Enum.StatusAnimal;
import com.reproequinos.vitaequus_api.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final AuthService authService;

    public AnimalService(
            AnimalRepository animalRepository,
            PropriedadeRepository propriedadeRepository,
            ProprietarioRepository proprietarioRepository,
            RacaRepository racaRepository,
            CuidadorPropriedadeRepository cuidadorRepository,
            MovimentacaoAnimalRepository movimentacaoRepository,
            AuthService authService
    ) {
        this.animalRepository = animalRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.proprietarioRepository = proprietarioRepository;
        this.racaRepository = racaRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
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
                .orElseThrow(() -> new RuntimeException("Propriedade não encontrada ou sem acesso"));

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
        animal.setStatus(dto.status() != null ? dto.status() : StatusAnimal.ativo);
        animal.setBiografia(dto.biografia());

        // 🔹 RAÇA (você já tem, mas mantendo aqui organizado)
        if (dto.racaId() != null) {
            Raca raca = racaRepository.findById(dto.racaId())
                    .orElseThrow(() -> new RuntimeException("Raça não encontrada"));
            animal.setRaca(raca);
        }

// 🔹 PROPRIETÁRIO
        if (dto.proprietarioId() != null) {
            Proprietario proprietario = proprietarioRepository.findById(dto.proprietarioId())
                    .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));

            animal.setProprietario(proprietario);
        }

// 🔹 CUIDADOR
        if (dto.cuidadorPropriedadeId() != null) {
            CuidadorPropriedade cuidador = cuidadorRepository.findById(dto.cuidadorPropriedadeId())
                    .orElseThrow(() -> new RuntimeException("Cuidador não encontrado"));

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
            Propriedade propriedade = propriedadeRepository.findById(dto.propriedadeId())
                    .orElseThrow(() -> new RuntimeException("Propriedade não encontrada"));
            animal.setPropriedade(propriedade);
        }

        if (dto.racaId() != null) {
            Raca raca = racaRepository.findById(dto.racaId())
                    .orElseThrow(() -> new RuntimeException("Raça não encontrada"));
            animal.setRaca(raca);
        }

        if (dto.proprietarioId() != null) {
            Proprietario proprietario = proprietarioRepository.findById(dto.proprietarioId())
                    .orElseThrow(() -> new RuntimeException("Proprietário não encontrado"));
            animal.setProprietario(proprietario);
        }

        if (dto.cuidadorPropriedadeId() != null) {
            CuidadorPropriedade cuidador = cuidadorRepository.findById(dto.cuidadorPropriedadeId())
                    .orElseThrow(() -> new RuntimeException("Cuidador da propriedade não encontrado"));
            animal.setCuidadorPropriedade(cuidador);
        }

        return toResponse(animal);
    }

    @Transactional
    public FotoResponseDTO uploadFoto(Long id, MultipartFile file) {
        Animal animal = buscarAnimal(id);

        try {
            String pasta = "uploads/animais/";
            new File(pasta).mkdirs();

            String nome = "animal_" + id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(pasta + nome);

            Files.write(path, file.getBytes());

            String url = "/uploads/animais/" + nome;

            animal.setUrlFoto(url);

            return new FotoResponseDTO(url);

        } catch (Exception e) {
            throw new RuntimeException("Erro upload");
        }
    }

    @Transactional
    public AnimalResponseDTO atualizarStatus(Long id, StatusAnimal status) {
        Animal animal = buscarAnimal(id);
        animal.setStatus(status);
        return toResponse(animal);
    }

    public List<TimelineEventoDTO> timeline(
            Long id,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String tipo
    ) {
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

        return lista.stream()
                .filter(e -> dataInicio == null || !e.dataHora().isBefore(dataInicio))
                .filter(e -> dataFim == null || !e.dataHora().isAfter(dataFim))
                .filter(e -> tipo == null || e.tipo().equalsIgnoreCase(tipo))
                .sorted(Comparator.comparing(TimelineEventoDTO::dataHora).reversed())
                .toList();
    }

    @Transactional
    public MovimentacaoResponseDTO registrarMovimentacao(Long animalId, MovimentacaoRequestDTO dto) {
        Veterinario vet = authService.getVeterinarioLogado();

        Animal animal = buscarAnimal(animalId);

        Propriedade novaPropriedade = propriedadeRepository.findById(dto.propriedadeId())
                .orElseThrow(() -> new RuntimeException("Propriedade não encontrada"));

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
                .orElseThrow(() -> new RuntimeException("Animal não encontrado ou sem acesso"));
    }

    public AnimalResponseDTO buscarPorId(Long id) {
        Animal animal = buscarAnimal(id); // já valida veterinário

        return toResponse(animal);
    }

    private void validarCategoriaSexo(Categoria categoria, Sexo sexo) {
        if (categoria == Categoria.Garanhao && sexo != Sexo.M) {
            throw new RuntimeException("Garanhão deve ser macho");
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
                null,
                null,
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