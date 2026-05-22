package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Response.AlertaReprodutivoDTO;
import com.reproequinos.vitaequus_api.Dto.Response.AnimalStatusReprodutivoDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardCardsDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardReprodutivoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.StatusReprodutivoAtualDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.Cobertura;
import com.reproequinos.vitaequus_api.entities.ExameReprodutivo;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.entities.Enum.EdemaUterino;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.AnimalRepository;
import com.reproequinos.vitaequus_api.repositories.CoberturaRepository;
import com.reproequinos.vitaequus_api.repositories.ExameReprodutivoRepository;
import com.reproequinos.vitaequus_api.repositories.GestacaoRepository;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import com.reproequinos.vitaequus_api.specifications.AnimalSpecifications;
import com.reproequinos.vitaequus_api.specifications.CoberturaSpecifications;
import com.reproequinos.vitaequus_api.specifications.GestacaoSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardReprodutivoService {

    private static final BigDecimal DIAMETRO_ALERTA_ULTRASSOM = BigDecimal.valueOf(28);

    private final AnimalRepository animalRepository;
    private final ExameReprodutivoRepository exameReprodutivoRepository;
    private final CoberturaRepository coberturaRepository;
    private final GestacaoRepository gestacaoRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final AuthService authService;

    public DashboardReprodutivoService(
            AnimalRepository animalRepository,
            ExameReprodutivoRepository exameReprodutivoRepository,
            CoberturaRepository coberturaRepository,
            GestacaoRepository gestacaoRepository,
            PropriedadeRepository propriedadeRepository,
            AuthService authService
    ) {
        this.animalRepository = animalRepository;
        this.exameReprodutivoRepository = exameReprodutivoRepository;
        this.coberturaRepository = coberturaRepository;
        this.gestacaoRepository = gestacaoRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public DashboardReprodutivoResponseDTO buscarDashboard(Long propriedadeId, LocalDate dataReferencia) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        LocalDate referencia = dataReferencia != null ? dataReferencia : LocalDate.now();

        if (propriedadeId != null) {
            propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(propriedadeId, veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Propriedade nao encontrada"));
        }

        List<Animal> animais = animalRepository.findAll(
                AnimalSpecifications.dashboardReprodutivos(
                        veterinarioId,
                        List.of(Categoria.EGUA, Categoria.RECEPTORA),
                        propriedadeId
                ),
                Sort.by(Sort.Direction.ASC, "nome")
        );

        List<Long> animalIds = animais.stream().map(Animal::getId).toList();
        Map<Long, ExameReprodutivo> ultimosExames = buscarUltimosExames(veterinarioId, animalIds, referencia);
        List<Cobertura> coberturasSemGestacao = buscarCoberturasSemGestacao(veterinarioId, propriedadeId, referencia);
        List<Gestacao> prenhesEmAndamento = gestacaoRepository.findAll(
                GestacaoSpecifications.prenhesEmAndamento(veterinarioId, propriedadeId),
                Sort.by(Sort.Direction.DESC, "dataDiagnosticoInicial")
        );

        Map<Long, Cobertura> coberturaPorAnimal = coberturasSemGestacao.stream()
                .collect(Collectors.toMap(
                        cobertura -> cobertura.getDoadora().getAnimal().getId(),
                        Function.identity(),
                        (maisRecente, ignorada) -> maisRecente
                ));
        Map<Long, Gestacao> gestacaoPorAnimal = prenhesEmAndamento.stream()
                .collect(Collectors.toMap(
                        gestacao -> gestacao.getDoadora().getAnimal().getId(),
                        Function.identity(),
                        (maisRecente, ignorada) -> maisRecente
                ));

        List<AnimalStatusReprodutivoDTO> vazias = new ArrayList<>();
        List<AnimalStatusReprodutivoDTO> acompanhamento = new ArrayList<>();
        List<AnimalStatusReprodutivoDTO> aguardandoDiagnostico = new ArrayList<>();
        List<AnimalStatusReprodutivoDTO> prenhes = new ArrayList<>();

        for (Animal animal : animais) {
            Gestacao gestacao = gestacaoPorAnimal.get(animal.getId());
            if (gestacao != null) {
                prenhes.add(toStatus(animal, descricaoGestacao(gestacao, referencia)));
                continue;
            }

            Cobertura cobertura = coberturaPorAnimal.get(animal.getId());
            if (cobertura != null) {
                long dias = diasDesde(cobertura.getDataHora().toLocalDate(), referencia);
                aguardandoDiagnostico.add(toStatus(animal, dias + " dias pós-cobertura"));
                continue;
            }

            ExameReprodutivo ultimoExame = ultimosExames.get(animal.getId());
            if (isExameRecenteEmAcompanhamento(ultimoExame, referencia)) {
                acompanhamento.add(toStatus(animal, descricaoExame(ultimoExame)));
                continue;
            }

            vazias.add(toStatus(animal, descricaoVazia(ultimoExame)));
        }

        List<AlertaReprodutivoDTO> alertas = new ArrayList<>();
        alertas.addAll(alertasUltrassomHoje(acompanhamento, ultimosExames));
        alertas.addAll(alertasDiagnosticoPendente(coberturasSemGestacao, referencia));
        alertas.addAll(alertasParto(prenhesEmAndamento, referencia));

        StatusReprodutivoAtualDTO statusAtual = new StatusReprodutivoAtualDTO(
                vazias,
                acompanhamento,
                aguardandoDiagnostico,
                prenhes
        );

        DashboardCardsDTO cards = new DashboardCardsDTO(
                vazias.size(),
                acompanhamento.size(),
                prenhes.size()
        );

        return new DashboardReprodutivoResponseDTO(cards, statusAtual, alertas);
    }

    private Map<Long, ExameReprodutivo> buscarUltimosExames(
            Long veterinarioId,
            List<Long> animalIds,
            LocalDate dataReferencia
    ) {
        if (animalIds.isEmpty()) {
            return Map.of();
        }

        return exameReprodutivoRepository
                .findUltimosExamesPorAnimais(veterinarioId, animalIds, dataReferencia.atTime(LocalTime.MAX))
                .stream()
                .collect(Collectors.toMap(exame -> exame.getAnimal().getId(), Function.identity()));
    }

    private List<Cobertura> buscarCoberturasSemGestacao(
            Long veterinarioId,
            Long propriedadeId,
            LocalDate dataReferencia
    ) {
        LocalDateTime inicio = dataReferencia.minusDays(20).atStartOfDay();
        LocalDateTime fim = dataReferencia.minusDays(10).atTime(LocalTime.MAX);
        return coberturaRepository.findAll(
                CoberturaSpecifications.semGestacaoNoPeriodo(veterinarioId, propriedadeId, inicio, fim),
                Sort.by(Sort.Direction.DESC, "dataHora")
        );
    }

    private boolean isExameRecenteEmAcompanhamento(ExameReprodutivo exame, LocalDate dataReferencia) {
        if (exame == null || exame.getDataHora().toLocalDate().isBefore(dataReferencia.minusDays(7))) {
            return false;
        }

        return temFoliculo(exame) || (exame.getEdemaUterino() != null && exame.getEdemaUterino() != EdemaUterino.AUSENTE);
    }

    private boolean temFoliculo(ExameReprodutivo exame) {
        return exame.getDiametroFolicular() != null && exame.getDiametroFolicular().compareTo(BigDecimal.ZERO) > 0;
    }

    private String descricaoExame(ExameReprodutivo exame) {
        List<String> partes = new ArrayList<>();
        if (temFoliculo(exame)) {
            partes.add("Folículo " + formatarMilimetros(exame.getDiametroFolicular()) + "mm");
        }
        if (exame.getEdemaUterino() != null && exame.getEdemaUterino() != EdemaUterino.AUSENTE) {
            partes.add("Edema " + formatarEdema(exame.getEdemaUterino()));
        }
        return partes.isEmpty() ? "Em acompanhamento folicular" : String.join(" - ", partes);
    }

    private String descricaoVazia(ExameReprodutivo ultimoExame) {
        if (ultimoExame == null) {
            return "Aguardando cio";
        }
        return "Sem atividade folicular";
    }

    private String descricaoGestacao(Gestacao gestacao, LocalDate dataReferencia) {
        LocalDate dataBase = gestacao.getDataDiagnosticoInicial();
        if (dataBase == null && gestacao.getCobertura() != null && gestacao.getCobertura().getDataHora() != null) {
            dataBase = gestacao.getCobertura().getDataHora().toLocalDate();
        }
        long dias = dataBase != null ? diasDesde(dataBase, dataReferencia) : 0;
        return "Gestação: " + dias + " dias";
    }

    private List<AlertaReprodutivoDTO> alertasUltrassomHoje(
            List<AnimalStatusReprodutivoDTO> animaisEmAcompanhamento,
            Map<Long, ExameReprodutivo> ultimosExames
    ) {
        return animaisEmAcompanhamento.stream()
                .map(status -> Map.entry(status, ultimosExames.get(status.animalId())))
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> entry.getValue().getDiametroFolicular() != null)
                .filter(entry -> entry.getValue().getDiametroFolicular().compareTo(DIAMETRO_ALERTA_ULTRASSOM) >= 0)
                .map(entry -> new AlertaReprodutivoDTO(
                        "ULTRASSOM_HOJE",
                        "Ultrassom Hoje",
                        entry.getKey().animalId(),
                        entry.getKey().nome(),
                        "Verificação de ovulação.",
                        "MEDIA"
                ))
                .toList();
    }

    private List<AlertaReprodutivoDTO> alertasDiagnosticoPendente(
            List<Cobertura> coberturasSemGestacao,
            LocalDate dataReferencia
    ) {
        return coberturasSemGestacao.stream()
                .filter(cobertura -> {
                    long dias = diasDesde(cobertura.getDataHora().toLocalDate(), dataReferencia);
                    return dias >= 12 && dias <= 18;
                })
                .collect(Collectors.toMap(
                        cobertura -> cobertura.getDoadora().getAnimal().getId(),
                        Function.identity(),
                        (maisRecente, ignorada) -> maisRecente
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(Cobertura::getDataHora).reversed())
                .map(cobertura -> {
                    Animal animal = cobertura.getDoadora().getAnimal();
                    long dias = diasDesde(cobertura.getDataHora().toLocalDate(), dataReferencia);
                    return new AlertaReprodutivoDTO(
                            "DIAGNOSTICO_PENDENTE",
                            "Diagnóstico Pendente",
                            animal.getId(),
                            animal.getNome(),
                            "DGV " + dias + ".",
                            "ALTA"
                    );
                })
                .toList();
    }

    private List<AlertaReprodutivoDTO> alertasParto(List<Gestacao> gestacoes, LocalDate dataReferencia) {
        return gestacoes.stream()
                .map(gestacao -> Map.entry(gestacao, diasGestacao(gestacao, dataReferencia)))
                .filter(entry -> entry.getValue() >= 320)
                .map(entry -> {
                    Animal animal = entry.getKey().getDoadora().getAnimal();
                    return new AlertaReprodutivoDTO(
                            "ALERTA_PARTO",
                            "Alerta de Parto",
                            animal.getId(),
                            animal.getNome(),
                            entry.getValue() + " dias de gestação.",
                            "ALTA"
                    );
                })
                .toList();
    }

    private long diasGestacao(Gestacao gestacao, LocalDate dataReferencia) {
        LocalDate dataBase = gestacao.getDataDiagnosticoInicial();
        if (dataBase == null && gestacao.getCobertura() != null && gestacao.getCobertura().getDataHora() != null) {
            dataBase = gestacao.getCobertura().getDataHora().toLocalDate();
        }
        return dataBase != null ? diasDesde(dataBase, dataReferencia) : 0;
    }

    private AnimalStatusReprodutivoDTO toStatus(Animal animal, String descricao) {
        return new AnimalStatusReprodutivoDTO(
                animal.getId(),
                animal.getNome(),
                iniciais(animal.getNome()),
                animal.getPropriedade().getId(),
                animal.getPropriedade().getNome(),
                null,
                descricao
        );
    }

    private String iniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "";
        }

        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }

    private String formatarMilimetros(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }

    private String formatarEdema(EdemaUterino edema) {
        return edema.name().replace('_', ' ');
    }

    private long diasDesde(LocalDate dataInicio, LocalDate dataFim) {
        return ChronoUnit.DAYS.between(dataInicio, dataFim);
    }
}
