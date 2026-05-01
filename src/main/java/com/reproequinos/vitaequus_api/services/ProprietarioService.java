package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.ProprietarioRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VincularPropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.ProprietarioResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.VinculoResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Resumo.PropriedadeResumoDTO;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Proprietario;
import com.reproequinos.vitaequus_api.entities.ProprietarioPropriedade;
import com.reproequinos.vitaequus_api.auth.AuthService;

import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import com.reproequinos.vitaequus_api.repositories.ProprietarioPropriedadeRepository;
import com.reproequinos.vitaequus_api.repositories.ProprietarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class ProprietarioService {

    private final ProprietarioRepository proprietarioRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final ProprietarioPropriedadeRepository proprietarioPropriedadeRepository;
    private final AuthService authService;

    public ProprietarioService(ProprietarioRepository proprietarioRepository,
                               PropriedadeRepository propriedadeRepository,
                               ProprietarioPropriedadeRepository proprietarioPropriedadeRepository,
                               AuthService authService) {
        this.proprietarioRepository = proprietarioRepository;
        this.propriedadeRepository = propriedadeRepository;
        this.proprietarioPropriedadeRepository = proprietarioPropriedadeRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<ProprietarioResponseDTO> listar() {

        Long veterinarioId = authService.getVeterinarioLogadoId();

        return proprietarioPropriedadeRepository.findAll()
                .stream()
                .map(ProprietarioPropriedade::getProprietario)
                .distinct()
                .filter(p -> p.getProprietarioPropriedades()
                        .stream()
                        .anyMatch(v ->
                                v.getPropriedade().getVeterinario().getId().equals(veterinarioId)
                        )
                )
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProprietarioResponseDTO buscarPorId(Long id) {
        Proprietario proprietario = buscarEntidadePorId(id);
        return toResponseDTO(proprietario);
    }

    @Transactional
    public ProprietarioResponseDTO criar(ProprietarioRequestDTO dto) {
        if (proprietarioRepository.existsByNrDocumento(dto.getNrDocumento())) {
            throw new IllegalArgumentException("Já existe proprietário com este número de documento");
        }

        Proprietario proprietario = new Proprietario();
        proprietario.setNome(dto.getNome());
        proprietario.setTipoDocumento(dto.getTipoDocumento());
        proprietario.setNrDocumento(dto.getNrDocumento());
        proprietario.setTelefone(dto.getTelefone());
        proprietario.setEmail(dto.getEmail());

        Proprietario salvo = proprietarioRepository.save(proprietario);
        return toResponseDTO(salvo);
    }

    @Transactional
    public ProprietarioResponseDTO atualizar(Long id, ProprietarioRequestDTO dto) {
        Proprietario proprietario = buscarEntidadePorId(id);

        if (proprietarioRepository.existsByNrDocumentoAndIdNot(dto.getNrDocumento(), id)) {
            throw new IllegalArgumentException("Já existe outro proprietário com este número de documento");
        }

        proprietario.setNome(dto.getNome());
        proprietario.setTipoDocumento(dto.getTipoDocumento());
        proprietario.setNrDocumento(dto.getNrDocumento());
        proprietario.setTelefone(dto.getTelefone());
        proprietario.setEmail(dto.getEmail());

        Proprietario atualizado = proprietarioRepository.save(proprietario);
        return toResponseDTO(atualizado);
    }

    @Transactional
    public VinculoResponseDTO vincularPropriedade(Long proprietarioId, VincularPropriedadeRequestDTO dto) {
        Proprietario proprietario = buscarEntidadePorId(proprietarioId);

        Propriedade propriedade = propriedadeRepository.findById(dto.getIdPropriedade())
                .orElseThrow(() -> new IllegalArgumentException("Propriedade não encontrada"));

        if (Boolean.FALSE.equals(propriedade.getAtivo())) {
            throw new IllegalArgumentException("Propriedade não encontrada");
        }

        boolean existe = proprietarioPropriedadeRepository
                .existsByProprietarioIdAndPropriedadeId(proprietarioId, dto.getIdPropriedade());

        if (existe) {
            throw new IllegalArgumentException("Este proprietário já está vinculado a esta propriedade");
        }

        ProprietarioPropriedade vinculo = new ProprietarioPropriedade();
        vinculo.setProprietario(proprietario);
        vinculo.setPropriedade(propriedade);
        vinculo.setTipoVinculo(dto.getTipoVinculo());

        ProprietarioPropriedade salvo = proprietarioPropriedadeRepository.save(vinculo);
        return toVinculoResponseDTO(salvo);
    }

    @Transactional
    public void removerVinculo(Long proprietarioId, Long propriedadeId) {
        buscarEntidadePorId(proprietarioId);

        ProprietarioPropriedade vinculo = proprietarioPropriedadeRepository
                .findByProprietarioIdAndPropriedadeId(proprietarioId, propriedadeId)
                .orElseThrow(() -> new IllegalArgumentException("Vínculo não encontrado"));

        proprietarioPropriedadeRepository.delete(vinculo);
    }

    @Transactional(readOnly = true)
    public List<PropriedadeResumoDTO> listarPropriedadesDoProprietario(Long proprietarioId) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        buscarEntidadePorId(proprietarioId);

        return proprietarioPropriedadeRepository.findByProprietarioId(proprietarioId)
                .stream()
                .map(ProprietarioPropriedade::getPropriedade)
                .filter(propriedade -> Boolean.TRUE.equals(propriedade.getAtivo()))
                .filter(propriedade -> propriedade.getVeterinario().getId().equals(veterinarioId))
                .map(this::toPropriedadeResumoDTO)
                .toList();
    }

    @Transactional
    public void deletar(Long id) {
        Proprietario proprietario = proprietarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proprietário não encontrado"));

        if (proprietarioPropriedadeRepository
                .existsByProprietarioIdAndPropriedade_AtivoTrue(id)) {

            throw new IllegalArgumentException(
                    "Não é possível excluir o proprietário pois ele possui propriedades ativas vinculadas"
            );
        }

        if (proprietario.getAnimais() != null && !proprietario.getAnimais().isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir o proprietário pois ele possui animais vinculados");
        }

        proprietarioRepository.delete(proprietario);
    }

    private Proprietario buscarEntidadePorId(Long id) {
        return proprietarioRepository.findById(id)
                .orElseThrow(() ->  new IllegalArgumentException("Proprietário não encontrado"));
    }

    private ProprietarioResponseDTO toResponseDTO(Proprietario entity) {
        ProprietarioResponseDTO dto = new ProprietarioResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setTipoDocumento(entity.getTipoDocumento());
        dto.setNrDocumento(entity.getNrDocumento());
        dto.setTelefone(entity.getTelefone());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    private PropriedadeResumoDTO toPropriedadeResumoDTO(Propriedade entity) {
        PropriedadeResumoDTO dto = new PropriedadeResumoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCidade(entity.getCidade());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    private VinculoResponseDTO toVinculoResponseDTO(ProprietarioPropriedade entity) {
        VinculoResponseDTO dto = new VinculoResponseDTO();
        dto.setId(entity.getId());
        dto.setIdProprietario(entity.getProprietario().getId());
        dto.setIdPropriedade(entity.getPropriedade().getId());
        dto.setTipoVinculo(entity.getTipoVinculo());
        return dto;
    }
}