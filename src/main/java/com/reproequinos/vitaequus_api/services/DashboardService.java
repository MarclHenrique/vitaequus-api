package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Response.DashboardAgendaItemDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardEstoqueDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardGeralResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardResumoCardDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardStatusItemDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardStatusReprodutivoDTO;
import com.reproequinos.vitaequus_api.Dto.Response.DashboardTaxaPrenhezDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Animal;
import com.reproequinos.vitaequus_api.entities.CheckupGestacional;
import com.reproequinos.vitaequus_api.entities.Cobertura;
import com.reproequinos.vitaequus_api.entities.ExameReprodutivo;
import com.reproequinos.vitaequus_api.entities.Gestacao;
import com.reproequinos.vitaequus_api.entities.Insumo;
import com.reproequinos.vitaequus_api.entities.Enum.Categoria;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.AnimalRepository;
import com.reproequinos.vitaequus_api.repositories.CheckupGestacionalRepository;
import com.reproequinos.vitaequus_api.repositories.CoberturaRepository;
import com.reproequinos.vitaequus_api.repositories.ExameReprodutivoRepository;
import com.reproequinos.vitaequus_api.repositories.GestacaoRepository;
import com.reproequinos.vitaequus_api.repositories.InsumoRepository;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import com.reproequinos.vitaequus_api.specifications.AnimalSpecifications;
import com.reproequinos.vitaequus_api.specifications.CheckupGestacionalSpecifications;
import com.reproequinos.vitaequus_api.specifications.CoberturaSpecifications;
import com.reproequinos.vitaequus_api.specifications.ExameReprodutivoSpecifications;
import com.reproequinos.vitaequus_api.specifications.GestacaoSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DashboardService {

    private static final BigDecimal DIAMETRO_ALERTA_ULTRASSOM = BigDecimal.valueOf(28);
    private static final List<Categoria> CATEGORIAS_MATRIZES = List.of(Categoria.EGUA, Categoria.RECEPTORA);
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final AnimalRepository animalRepository;
    private final GestacaoRepository gestacaoRepository;
    private final InsumoRepository insumoRepository;
    private final ExameReprodutivoRepository exameReprodutivoRepository;
    private final CoberturaRepository coberturaRepository;
    private final CheckupGestacionalRepository checkupGestacionalRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final AuthService authService;

    public DashboardService(
            AnimalRepository animalRepository,
            GestacaoRepository gestacaoRepository,
            InsumoRepository insumoRepository,
            ExameReprodutivoRepository exameReprodutivoRepository,
            CoberturaRepository coberturaRepository,
            CheckupGestacionalRepository checkupGestacionalRepository,
            PropriedadeRepository propriedadeRepository,
            AuthService authService
    ) {
        this.animalRepository = animalRepository;
        this.gestacaoRepository = gestacaoRepository;
        this.insumoRepository = insumoRepository;
        this.exameReprodutivoRepository = exameReprodutivoRepository;
        this.coberturaRepository = coberturaRepository;
        this.checkupGestacionalRepository = checkupGestacionalRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public DashboardGeralResponseDTO buscarDashboard(Long propriedadeId, LocalDate dataReferencia) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        LocalDate referencia = dataReferencia != null ? dataReferencia : LocalDate.now();

        if (propriedadeId != null) {
            propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(propriedadeId, veterinarioId)
                    .orElseThrow(() -> new NotFoundException("Propriedade nao encontrada"));
        }

        long plantelAtivo = animalRepository.count(AnimalSpecifications.dashboardAtivos(veterinarioId, propriedadeId));
        long totalMatrizes = animalRepository.count(
                AnimalSpecifications.dashboardMatrizes(veterinarioId, CATEGORIAS_MATRIZES, propriedadeId)
        );
        long prenhes = gestacaoRepository.count(GestacaoSpecifications.prenhesEmAndamento(veterinarioId, propriedadeId));
        long alertasEstoque = insumoRepository.countAlertasEstoqueDashboard(veterinarioId, referencia.plusDays(30));

        DashboardResumoCardDTO plantelAtivoDto = new DashboardResumoCardDTO(
                plantelAtivo,
                propriedadeId != null ? "Animais na propriedade selecionada" : "Animais ativos cadastrados"
        );
        DashboardTaxaPrenhezDTO taxaPrenhez = new DashboardTaxaPrenhezDTO(
                percentual(prenhes, totalMatrizes),
                prenhes,
                totalMatrizes,
                prenhes + " Éguas prenhes / " + totalMatrizes + " Total de matrizes"
        );
        DashboardEstoqueDTO estoque = new DashboardEstoqueDTO(
                alertasEstoque,
                "Insumos com estoque baixo ou vencidos"
        );

        return new DashboardGeralResponseDTO(
                plantelAtivoDto,
                taxaPrenhez,
                estoque,
                montarAgenda(veterinarioId, propriedadeId, referencia),
                montarStatusReprodutivo(veterinarioId, propriedadeId, referencia, totalMatrizes, prenhes)
        );
    }

    private DashboardStatusReprodutivoDTO montarStatusReprodutivo(
            Long veterinarioId,
            Long propriedadeId,
            LocalDate referencia,
            long totalMonitorado,
            long prenhezConfirmada
    ) {
        LocalDateTime inicioExames = referencia.minusDays(7).atStartOfDay();
        LocalDateTime fimReferencia = referencia.atTime(LocalTime.MAX);

        Set<Long> emAcompanhamentoIds = exameReprodutivoRepository
                .findAll(ExameReprodutivoSpecifications.dashboardPeriodo(
                        veterinarioId, propriedadeId, CATEGORIAS_MATRIZES, inicioExames, fimReferencia
                ))
                .stream()
                .map(exame -> exame.getAnimal().getId())
                .collect(java.util.stream.Collectors.toCollection(HashSet::new));

        List<Cobertura> coberturasSemDiagnostico = buscarCoberturasSemDiagnostico(
                veterinarioId,
                propriedadeId,
                referencia,
                10,
                20
        );
        coberturasSemDiagnostico.stream()
                .map(cobertura -> cobertura.getDoadora().getAnimal().getId())
                .forEach(emAcompanhamentoIds::add);

        Set<Long> prenhesIds = new HashSet<>();
        buscarPrenhesEmAndamento(veterinarioId, propriedadeId).stream()
                .map(gestacao -> gestacao.getDoadora().getAnimal().getId())
                .forEach(prenhesIds::add);
        emAcompanhamentoIds.removeAll(prenhesIds);

        long emAcompanhamento = emAcompanhamentoIds.size();
        long eguasVazias = Math.max(0, totalMonitorado - emAcompanhamento - prenhezConfirmada);

        return new DashboardStatusReprodutivoDTO(
                statusItem(eguasVazias, totalMonitorado),
                statusItem(emAcompanhamento, totalMonitorado),
                statusItem(prenhezConfirmada, totalMonitorado),
                totalMonitorado,
                "Total Monitorado: " + totalMonitorado + " Receptores/Matrizes"
        );
    }

    private DashboardStatusItemDTO statusItem(long total, long totalMonitorado) {
        int percentual = percentual(total, totalMonitorado);
        return new DashboardStatusItemDTO(total, percentual, total + " matrizes (" + percentual + "%)");
    }

    private List<DashboardAgendaItemDTO> montarAgenda(Long veterinarioId, Long propriedadeId, LocalDate referencia) {
        List<DashboardAgendaItemDTO> itens = new ArrayList<>();
        itens.addAll(alertasUltrassom(veterinarioId, propriedadeId, referencia));
        itens.addAll(alertasDiagnostico(veterinarioId, propriedadeId, referencia));
        itens.addAll(alertasParto(veterinarioId, propriedadeId, referencia));
        itens.addAll(alertasCheckup(veterinarioId, propriedadeId, referencia));
        itens.addAll(alertasEstoque(veterinarioId, referencia));

        return itens.stream()
                .sorted(Comparator
                        .comparing(DashboardAgendaItemDTO::dataHora)
                        .thenComparing(item -> prioridadePeso(item.prioridade())))
                .limit(10)
                .toList();
    }

    private List<DashboardAgendaItemDTO> alertasUltrassom(Long veterinarioId, Long propriedadeId, LocalDate referencia) {
        LocalDateTime inicio = referencia.minusDays(2).atStartOfDay();
        LocalDateTime fim = referencia.atTime(LocalTime.MAX);
        return exameReprodutivoRepository
                .findAll(
                        ExameReprodutivoSpecifications.dashboardUltrassom(
                                veterinarioId,
                                propriedadeId,
                                CATEGORIAS_MATRIZES,
                                DIAMETRO_ALERTA_ULTRASSOM,
                                inicio,
                                fim
                        ),
                        Sort.by(Sort.Direction.ASC, "dataHora")
                )
                .stream()
                .map(exame -> {
                    Animal animal = exame.getAnimal();
                    return new DashboardAgendaItemDTO(
                            "ULTRASSOM",
                            "Ultrassom marcado para égua " + animal.getNome() + " (verificação de ovulação).",
                            quando(exame.getDataHora(), referencia),
                            exame.getPropriedade().getNome(),
                            "MEDIA",
                            exame.getDataHora()
                    );
                })
                .toList();
    }

    private List<DashboardAgendaItemDTO> alertasDiagnostico(Long veterinarioId, Long propriedadeId, LocalDate referencia) {
        return buscarCoberturasSemDiagnostico(veterinarioId, propriedadeId, referencia, 12, 18).stream()
                .map(cobertura -> {
                    Animal animal = cobertura.getDoadora().getAnimal();
                    long dias = ChronoUnit.DAYS.between(cobertura.getDataHora().toLocalDate(), referencia);
                    return new DashboardAgendaItemDTO(
                            "DIAGNOSTICO_PENDENTE",
                            "Diagnóstico pendente para égua " + animal.getNome() + " (" + dias + " dias pós-cobertura).",
                            quando(cobertura.getDataHora(), referencia),
                            cobertura.getPropriedade().getNome(),
                            "ALTA",
                            cobertura.getDataHora()
                    );
                })
                .toList();
    }

    private List<DashboardAgendaItemDTO> alertasParto(Long veterinarioId, Long propriedadeId, LocalDate referencia) {
        return buscarPrenhesEmAndamento(veterinarioId, propriedadeId).stream()
                .filter(gestacao -> isPartoProximo(gestacao, referencia))
                .map(gestacao -> {
                    Animal animal = gestacao.getDoadora().getAnimal();
                    LocalDateTime dataHora = dataHoraParto(gestacao, referencia);
                    return new DashboardAgendaItemDTO(
                            "PARTO_PROXIMO",
                            "Previsão de parto da égua " + animal.getNome() + " para os próximos 7 dias.",
                            quando(dataHora, referencia),
                            gestacao.getCobertura().getPropriedade().getNome(),
                            "ALTA",
                            dataHora
                    );
                })
                .toList();
    }

    private List<DashboardAgendaItemDTO> alertasCheckup(Long veterinarioId, Long propriedadeId, LocalDate referencia) {
        return checkupGestacionalRepository
                .findAll(
                        CheckupGestacionalSpecifications.proximosDashboard(
                                veterinarioId,
                                propriedadeId,
                                referencia.atStartOfDay(),
                                referencia.plusDays(7).atTime(LocalTime.MAX)
                        ),
                        Sort.by(Sort.Direction.ASC, "dataHora")
                )
                .stream()
                .map(checkup -> {
                    Animal animal = checkup.getGestacao().getDoadora().getAnimal();
                    return new DashboardAgendaItemDTO(
                            "CHECKUP_GESTACIONAL",
                            "Check-up gestacional agendado para " + animal.getNome() + ".",
                            quando(checkup.getDataHora(), referencia),
                            checkup.getGestacao().getCobertura().getPropriedade().getNome(),
                            "MEDIA",
                            checkup.getDataHora()
                    );
                })
                .toList();
    }

    private List<DashboardAgendaItemDTO> alertasEstoque(Long veterinarioId, LocalDate referencia) {
        return insumoRepository.findAlertasEstoqueDashboard(veterinarioId, referencia.plusDays(30)).stream()
                .map(insumo -> new DashboardAgendaItemDTO(
                        "ESTOQUE",
                        insumo.getNomeComercial() + " com estoque baixo ou vencimento próximo.",
                        quando(dataHoraInsumo(insumo, referencia), referencia),
                        null,
                        "ALTA",
                        dataHoraInsumo(insumo, referencia)
                ))
                .toList();
    }

    private List<Cobertura> buscarCoberturasSemDiagnostico(
            Long veterinarioId,
            Long propriedadeId,
            LocalDate referencia,
            int diasMinimo,
            int diasMaximo
    ) {
        LocalDateTime inicio = referencia.minusDays(diasMaximo).atStartOfDay();
        LocalDateTime fim = referencia.minusDays(diasMinimo).atTime(LocalTime.MAX);
        return coberturaRepository.findAll(
                CoberturaSpecifications.semGestacaoNoPeriodo(veterinarioId, propriedadeId, inicio, fim),
                Sort.by(Sort.Direction.DESC, "dataHora")
        );
    }

    private List<Gestacao> buscarPrenhesEmAndamento(Long veterinarioId, Long propriedadeId) {
        return gestacaoRepository.findAll(
                GestacaoSpecifications.prenhesEmAndamento(veterinarioId, propriedadeId),
                Sort.by(Sort.Direction.DESC, "dataDiagnosticoInicial")
        );
    }

    private boolean isPartoProximo(Gestacao gestacao, LocalDate referencia) {
        LocalDate previsao = gestacao.getDataPrevisaoParto();
        if (previsao != null && !previsao.isBefore(referencia) && !previsao.isAfter(referencia.plusDays(7))) {
            return true;
        }

        return diasGestacao(gestacao, referencia) >= 330;
    }

    private LocalDateTime dataHoraParto(Gestacao gestacao, LocalDate referencia) {
        if (gestacao.getDataPrevisaoParto() != null) {
            return gestacao.getDataPrevisaoParto().atStartOfDay();
        }
        return referencia.atStartOfDay();
    }

    private LocalDateTime dataHoraInsumo(Insumo insumo, LocalDate referencia) {
        if (insumo.getDataValidade() != null) {
            return insumo.getDataValidade().atStartOfDay();
        }
        return referencia.atStartOfDay();
    }

    private long diasGestacao(Gestacao gestacao, LocalDate referencia) {
        LocalDate dataBase = gestacao.getDataDiagnosticoInicial();
        if (dataBase == null && gestacao.getCobertura() != null && gestacao.getCobertura().getDataHora() != null) {
            dataBase = gestacao.getCobertura().getDataHora().toLocalDate();
        }
        return dataBase != null ? ChronoUnit.DAYS.between(dataBase, referencia) : 0;
    }

    private int percentual(long parte, long total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round((parte * 100.0) / total);
    }

    private String quando(LocalDateTime dataHora, LocalDate referencia) {
        if (dataHora.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            if (dataHora.toLocalDate().equals(referencia)) {
                return "Hoje";
            }
            if (dataHora.toLocalDate().equals(referencia.plusDays(1))) {
                return "Amanhã";
            }
        }

        if (dataHora.toLocalDate().equals(referencia)) {
            return "Hoje às " + dataHora.format(HORA_FORMATTER);
        }
        if (dataHora.toLocalDate().equals(referencia.plusDays(1))) {
            return "Amanhã às " + dataHora.format(HORA_FORMATTER);
        }
        return dataHora.format(DATA_HORA_FORMATTER);
    }

    private int prioridadePeso(String prioridade) {
        return "ALTA".equals(prioridade) ? 0 : 1;
    }
}
