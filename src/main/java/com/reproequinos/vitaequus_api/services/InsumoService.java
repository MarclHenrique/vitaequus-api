package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.InsumoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.InsumoResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Fornecedor;
import com.reproequinos.vitaequus_api.entities.Insumo;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.FornecedorRepository;
import com.reproequinos.vitaequus_api.repositories.InsumoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final AuthService authService;

    public InsumoService(
            InsumoRepository insumoRepository,
            FornecedorRepository fornecedorRepository,
            AuthService authService
    ) {
        this.insumoRepository = insumoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Page<InsumoResponseDTO> listar(TipoInsumo tipo, Long fornecedorId, Pageable pageable) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return insumoRepository.findByFiltros(veterinarioId, tipo, fornecedorId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InsumoResponseDTO buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public InsumoResponseDTO criar(InsumoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();
        Fornecedor fornecedor = buscarFornecedorDoVeterinario(dto.fornecedorId(), veterinarioId);

        Insumo insumo = new Insumo();
        insumo.setNomeComercial(dto.nomeComercial());
        insumo.setTipo(dto.tipo());
        insumo.setPrincipioAtivo(dto.principioAtivo());
        insumo.setFornecedor(fornecedor);
        insumo.setVeterinario(veterinario);

        return toResponse(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoResponseDTO atualizar(Long id, InsumoRequestDTO dto) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Insumo insumo = buscarEntidadePorId(id);
        Fornecedor fornecedor = buscarFornecedorDoVeterinario(dto.fornecedorId(), veterinarioId);

        insumo.setNomeComercial(dto.nomeComercial());
        insumo.setTipo(dto.tipo());
        insumo.setPrincipioAtivo(dto.principioAtivo());
        insumo.setFornecedor(fornecedor);

        return toResponse(insumo);
    }

    @Transactional
    public void deletar(Long id) {
        Insumo insumo = buscarEntidadePorId(id);
        insumoRepository.delete(insumo);
    }

    private Insumo buscarEntidadePorId(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return insumoRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Insumo nao encontrado"));
    }

    private Fornecedor buscarFornecedorDoVeterinario(Long fornecedorId, Long veterinarioId) {
        return fornecedorRepository.findByIdAndVeterinarioId(fornecedorId, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Fornecedor nao encontrado"));
    }

    private InsumoResponseDTO toResponse(Insumo insumo) {
        return new InsumoResponseDTO(
                insumo.getId(),
                insumo.getNomeComercial(),
                insumo.getTipo(),
                insumo.getPrincipioAtivo(),
                insumo.getFornecedor().getId(),
                insumo.getFornecedor().getNome()
        );
    }
}
