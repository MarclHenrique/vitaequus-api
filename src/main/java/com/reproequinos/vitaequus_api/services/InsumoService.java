package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.InsumoRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.InsumoResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Enum.TipoInsumo;
import com.reproequinos.vitaequus_api.entities.Fornecedor;
import com.reproequinos.vitaequus_api.entities.Insumo;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.FornecedorRepository;
import com.reproequinos.vitaequus_api.repositories.InsumoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
    public Page<InsumoResponseDTO> listar(
            TipoInsumo tipo,
            Long fornecedorId,
            Boolean estoqueBaixo,
            LocalDate vencendoAte,
            Pageable pageable
    ) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        if (fornecedorId != null) {
            buscarFornecedorDoVeterinario(fornecedorId, veterinarioId);
        }

        return insumoRepository.findByFiltros(veterinarioId, tipo, fornecedorId, estoqueBaixo, vencendoAte, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InsumoResponseDTO buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public InsumoResponseDTO criar(InsumoRequestDTO dto) {
        validarEstoques(dto);

        Long veterinarioId = authService.getVeterinarioLogadoId();
        Veterinario veterinario = authService.getVeterinarioLogado();
        Fornecedor fornecedor = buscarFornecedorDoVeterinario(dto.fornecedorId(), veterinarioId);

        Insumo insumo = new Insumo();
        preencherInsumo(insumo, dto);
        insumo.setFornecedor(fornecedor);
        insumo.setVeterinario(veterinario);

        return toResponse(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoResponseDTO atualizar(Long id, InsumoRequestDTO dto) {
        validarEstoques(dto);

        Long veterinarioId = authService.getVeterinarioLogadoId();
        Insumo insumo = buscarEntidadePorId(id);
        Fornecedor fornecedor = buscarFornecedorDoVeterinario(dto.fornecedorId(), veterinarioId);

        preencherInsumo(insumo, dto);
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

    private void preencherInsumo(Insumo insumo, InsumoRequestDTO dto) {
        insumo.setNomeComercial(dto.nomeComercial());
        insumo.setTipo(dto.tipo());
        insumo.setPrincipioAtivo(dto.principioAtivo());
        insumo.setUnidadeMedida(dto.unidadeMedida());
        insumo.setEstoqueAtual(dto.estoqueAtual());
        insumo.setEstoqueMinimo(dto.estoqueMinimo());
        insumo.setDataValidade(dto.dataValidade());
        insumo.setObservacoes(dto.observacoes());
    }

    private void validarEstoques(InsumoRequestDTO dto) {
        if (dto.estoqueAtual() != null && dto.estoqueAtual() < 0) {
            throw new BadRequestException("Estoque atual nao pode ser negativo");
        }

        if (dto.estoqueMinimo() != null && dto.estoqueMinimo() < 0) {
            throw new BadRequestException("Estoque minimo nao pode ser negativo");
        }
    }

    private InsumoResponseDTO toResponse(Insumo insumo) {
        return new InsumoResponseDTO(
                insumo.getId(),
                insumo.getNomeComercial(),
                insumo.getTipo(),
                insumo.getPrincipioAtivo(),
                insumo.getUnidadeMedida(),
                insumo.getEstoqueAtual(),
                insumo.getEstoqueMinimo(),
                insumo.getDataValidade(),
                insumo.getFornecedor().getId(),
                insumo.getFornecedor().getNome(),
                insumo.getObservacoes()
        );
    }
}
