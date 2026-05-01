package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.PropriedadeRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.PropriedadeSimplesRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDTO;
import com.reproequinos.vitaequus_api.Dto.Response.PropriedadeResponseDtov2;
import com.reproequinos.vitaequus_api.Dto.Resumo.ProprietarioResumoDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Propriedade;
import com.reproequinos.vitaequus_api.entities.Proprietario;
import com.reproequinos.vitaequus_api.entities.ProprietarioPropriedade;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.repositories.PropriedadeRepository;
import com.reproequinos.vitaequus_api.repositories.ProprietarioPropriedadeRepository;
import com.reproequinos.vitaequus_api.repositories.ProprietarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PropriedadeService {

    private final PropriedadeRepository propriedadeRepository;
    private final ProprietarioPropriedadeRepository proprietarioPropriedadeRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final AuthService authService;

    public PropriedadeService(PropriedadeRepository propriedadeRepository,
                              ProprietarioPropriedadeRepository proprietarioPropriedadeRepository,
                              ProprietarioRepository proprietarioRepository,
                              AuthService authService) {
        this.propriedadeRepository = propriedadeRepository;
        this.proprietarioPropriedadeRepository = proprietarioPropriedadeRepository;
        this.proprietarioRepository = proprietarioRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<PropriedadeResponseDTO> listar() {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return propriedadeRepository.findByAtivoTrueAndVeterinarioId(veterinarioId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PropriedadeResponseDTO buscarPorId(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        Propriedade propriedade = buscarEntidadeAtivaPorId(id, veterinarioId);
        return toResponseDTO(propriedade);
    }

    @Transactional
    public PropriedadeResponseDTO criar(PropriedadeRequestDTO dto) {
        Veterinario veterinario = authService.getVeterinarioLogado();

        Proprietario proprietario = proprietarioRepository.findById(dto.getProprietarioId())
                .orElseThrow(() -> new IllegalArgumentException("Proprietário não encontrado"));

        Propriedade propriedade = new Propriedade();
        propriedade.setNome(dto.getNome());
        propriedade.setTipoPropriedade(dto.getTipoPropriedade());
        propriedade.setEndereco(dto.getEndereco());
        propriedade.setCidade(dto.getCidade());
        propriedade.setEstado(dto.getEstado());
        propriedade.setCelular(dto.getCelular());
        propriedade.setEmail(dto.getEmail());
        propriedade.setAtivo(true);
        propriedade.setVeterinario(veterinario);

        Propriedade salva = propriedadeRepository.save(propriedade);

        ProprietarioPropriedade vinculo = new ProprietarioPropriedade();
        vinculo.setProprietario(proprietario);
        vinculo.setPropriedade(salva);
        vinculo.setTipoVinculo(dto.getTipoVinculo());

        proprietarioPropriedadeRepository.save(vinculo);

        return toResponseDTO(salva);
    }

    @Transactional
    public PropriedadeResponseDtov2 criarSimples(PropriedadeSimplesRequestDTO dto) {
        Veterinario veterinario = authService.getVeterinarioLogado();

        Propriedade propriedade = new Propriedade();
        propriedade.setNome(dto.getNome());
        propriedade.setTipoPropriedade(dto.getTipoPropriedade());
        propriedade.setEndereco(dto.getEndereco());
        propriedade.setCidade(dto.getCidade());
        propriedade.setEstado(dto.getEstado());
        propriedade.setCelular(dto.getCelular());
        propriedade.setEmail(dto.getEmail());
        propriedade.setAtivo(true);
        propriedade.setVeterinario(veterinario);

        Propriedade salva = propriedadeRepository.save(propriedade);

        return toResponseDTOV2(salva);
    }

    @Transactional
    public PropriedadeResponseDTO atualizar(Long id, PropriedadeRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        Propriedade propriedade = buscarEntidadeAtivaPorId(id, veterinarioId);

        propriedade.setNome(dto.getNome());
        propriedade.setTipoPropriedade(dto.getTipoPropriedade());
        propriedade.setEndereco(dto.getEndereco());
        propriedade.setCidade(dto.getCidade());
        propriedade.setEstado(dto.getEstado());
        propriedade.setCelular(dto.getCelular());
        propriedade.setEmail(dto.getEmail());

        Propriedade atualizada = propriedadeRepository.save(propriedade);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public void remover(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        Propriedade propriedade = buscarEntidadeAtivaPorId(id, veterinarioId);
        propriedade.setAtivo(false);
        propriedadeRepository.save(propriedade);
    }

    @Transactional(readOnly = true)
    public List<ProprietarioResumoDTO> listarProprietariosDaPropriedade(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        buscarEntidadeAtivaPorId(id, veterinarioId);

        return proprietarioPropriedadeRepository.findByPropriedadeId(id)
                .stream()
                .map(ProprietarioPropriedade::getProprietario)
                .map(this::toProprietarioResumoDTO)
                .toList();
    }

    private Propriedade buscarEntidadeAtivaPorId(Long id, Long veterinarioId) {
        return propriedadeRepository.findByIdAndAtivoTrueAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new IllegalArgumentException("Propriedade não encontrada"));
    }

    private PropriedadeResponseDTO toResponseDTO(Propriedade entity) {
        PropriedadeResponseDTO dto = new PropriedadeResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setTipoPropriedade(entity.getTipoPropriedade());
        dto.setEndereco(entity.getEndereco());
        dto.setCidade(entity.getCidade());
        dto.setEstado(entity.getEstado());
        dto.setCelular(entity.getCelular());
        dto.setEmail(entity.getEmail());
        dto.setAtivo(entity.getAtivo());
        return dto;
    }

    private PropriedadeResponseDtov2 toResponseDTOV2(Propriedade entity) {
        PropriedadeResponseDtov2 dto = new PropriedadeResponseDtov2();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setTipoPropriedade(entity.getTipoPropriedade());
        dto.setEndereco(entity.getEndereco());
        dto.setCidade(entity.getCidade());
        dto.setEstado(entity.getEstado());
        dto.setCelular(entity.getCelular());
        dto.setEmail(entity.getEmail());
        dto.setAtivo(entity.getAtivo());
        return dto;
    }

    private ProprietarioResumoDTO toProprietarioResumoDTO(Proprietario entity) {
        ProprietarioResumoDTO dto = new ProprietarioResumoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setNrDocumento(entity.getNrDocumento());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}