package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.CuidadorRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.StatusVinculoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VincularCuidadorPropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CuidadorPropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.CuidadorResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Cuidador;
import com.reproequinos.vitaequus_api.entities.CuidadorPropriedade;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.CuidadorPropriedadeRepository;
import com.reproequinos.vitaequus_api.repositories.CuidadorRepository;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import com.reproequinos.vitaequus_api.specifications.CuidadorSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuidadorService {

    private final CuidadorRepository cuidadorRepository;
    private final CuidadorPropriedadeRepository cuidadorPropriedadeRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final AuthService authService;

    public CuidadorService(
            CuidadorRepository cuidadorRepository,
            CuidadorPropriedadeRepository cuidadorPropriedadeRepository,
            PropriedadeRepository propriedadeRepository,
            AuthService authService
    ) {
        this.cuidadorRepository = cuidadorRepository;
        this.cuidadorPropriedadeRepository = cuidadorPropriedadeRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.authService = authService;
    }

    public Page<CuidadorResponseDTO> listar(String nome, String telefone, Boolean ativo, Pageable pageable) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return cuidadorRepository.findAll(
                        CuidadorSpecifications.filtros(veterinarioId, nome, telefone, ativo),
                        pageable
                )
                .map(this::toCuidadorResponse);
    }

    @Transactional
    public CuidadorResponseDTO criar(CuidadorRequestDTO dto) {
        if (cuidadorRepository.existsByNrDocumento(dto.nrDocumento())) {
            throw new BadRequestException("Já existe cuidador com esse documento");
        }

        Cuidador cuidador = new Cuidador();
        cuidador.setNome(dto.nome());
        cuidador.setTipoDocumento(dto.tipoDocumento());
        cuidador.setNrDocumento(dto.nrDocumento());
        cuidador.setTelefone(dto.telefone());
        cuidador.setEmail(dto.email());
        cuidador.setVeterinario(authService.getVeterinarioLogado());

        return toCuidadorResponse(cuidadorRepository.save(cuidador));
    }

    @Transactional
    public CuidadorResponseDTO atualizar(Long id, CuidadorRequestDTO dto) {
        Cuidador cuidador = buscarCuidador(id);

        cuidador.setNome(dto.nome());
        cuidador.setTipoDocumento(dto.tipoDocumento());
        cuidador.setNrDocumento(dto.nrDocumento());
        cuidador.setTelefone(dto.telefone());
        cuidador.setEmail(dto.email());

        return toCuidadorResponse(cuidador);
    }

    @Transactional
    public CuidadorPropriedadeResponseDTO vincularPropriedade(
            Long cuidadorId,
            VincularCuidadorPropriedadeRequestDTO dto
    ) {
        Cuidador cuidador = buscarCuidador(cuidadorId);

        Long veterinarioId = authService.getVeterinarioLogadoId();

        Propriedade propriedade = propriedadeRepository
                .findByIdAndAtivoTrueAndVeterinarioId(dto.propriedadeId(), veterinarioId)
                .orElseThrow(() -> new NotFoundException("Propriedade não encontrada ou sem acesso"));

        if (cuidadorPropriedadeRepository.existsByCuidadorIdAndPropriedadeId(cuidadorId, dto.propriedadeId())) {
            throw new BadRequestException("Cuidador já vinculado a esta propriedade");
        }

        CuidadorPropriedade vinculo = new CuidadorPropriedade();
        vinculo.setCuidador(cuidador);
        vinculo.setPropriedade(propriedade);
        vinculo.setStatus(0);

        return toVinculoResponse(cuidadorPropriedadeRepository.save(vinculo));
    }

    @Transactional
    public CuidadorPropriedadeResponseDTO atualizarStatusVinculo(
            Long cuidadorId,
            Long propriedadeId,
            StatusVinculoRequestDTO dto
    ) {
        if (dto.status() != 0 && dto.status() != 1) {
            throw new BadRequestException("Status inválido. Use 0 para ativo ou 1 para inativo");
        }

        CuidadorPropriedade vinculo = cuidadorPropriedadeRepository
                .findByCuidadorIdAndPropriedadeIdAndPropriedadeVeterinarioId(
                        cuidadorId,
                        propriedadeId,
                        authService.getVeterinarioLogadoId()
                )
                .orElseThrow(() -> new NotFoundException("Vínculo não encontrado ou sem acesso"));

        vinculo.setStatus(dto.status());

        return toVinculoResponse(vinculo);
    }

    public List<PropriedadeResponseDTO> listarPropriedadesDoCuidador(Long cuidadorId) {
        buscarCuidador(cuidadorId);

        Long veterinarioId = authService.getVeterinarioLogadoId();

        return cuidadorPropriedadeRepository.findByCuidadorIdAndPropriedadeVeterinarioId(cuidadorId, veterinarioId)
                .stream()
                .map(CuidadorPropriedade::getPropriedade)
                .map(this::toPropriedadeResponse)
                .toList();
    }

    private Cuidador buscarCuidador(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return cuidadorRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Cuidador não encontrado"));
    }

    private CuidadorResponseDTO toCuidadorResponse(Cuidador c) {
        return new CuidadorResponseDTO(
                c.getId(),
                c.getNome(),
                c.getTipoDocumento(),
                c.getNrDocumento(),
                c.getTelefone(),
                c.getEmail()
        );
    }

    private CuidadorPropriedadeResponseDTO toVinculoResponse(CuidadorPropriedade cp) {
        return new CuidadorPropriedadeResponseDTO(
                cp.getId(),
                cp.getCuidador().getId(),
                cp.getCuidador().getNome(),
                cp.getPropriedade().getId(),
                cp.getPropriedade().getNome(),
                cp.getStatus()
        );
    }

    private PropriedadeResponseDTO toPropriedadeResponse(Propriedade p) {
        PropriedadeResponseDTO dto = new PropriedadeResponseDTO();

        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setTipoPropriedade(p.getTipoPropriedade());
        dto.setEndereco(p.getEndereco());
        dto.setCidade(p.getCidade());
        dto.setEstado(p.getEstado());
        dto.setCelular(p.getCelular());
        dto.setEmail(p.getEmail());
        dto.setAtivo(p.getAtivo());

        return dto;
    }

}
