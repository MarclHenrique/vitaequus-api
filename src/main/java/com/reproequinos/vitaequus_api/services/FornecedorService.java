package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.FornecedorRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Response.FornecedorResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Fornecedor;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.exceptions.NotFoundException;
import com.reproequinos.vitaequus_api.repositories.FornecedorRepository;
import com.reproequinos.vitaequus_api.repositories.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final InsumoRepository insumoRepository;
    private final AuthService authService;

    public FornecedorService(
            FornecedorRepository fornecedorRepository,
            InsumoRepository insumoRepository,
            AuthService authService
    ) {
        this.fornecedorRepository = fornecedorRepository;
        this.insumoRepository = insumoRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> listar() {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return fornecedorRepository.findByVeterinarioId(veterinarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public FornecedorResponseDTO criar(FornecedorRequestDTO dto) {
        Veterinario veterinario = authService.getVeterinarioLogado();

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(dto.nome());
        fornecedor.setContato(dto.contato());
        fornecedor.setTelefone(dto.telefone());
        fornecedor.setEmail(dto.email());
        fornecedor.setVeterinario(veterinario);

        return toResponse(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponseDTO atualizar(Long id, FornecedorRequestDTO dto) {
        Fornecedor fornecedor = buscarEntidadePorId(id);

        fornecedor.setNome(dto.nome());
        fornecedor.setContato(dto.contato());
        fornecedor.setTelefone(dto.telefone());
        fornecedor.setEmail(dto.email());

        return toResponse(fornecedor);
    }

    @Transactional
    public void deletar(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();
        Fornecedor fornecedor = buscarEntidadePorId(id);

        if (insumoRepository.existsByFornecedorIdAndVeterinarioId(id, veterinarioId)) {
            throw new BadRequestException("Nao e possivel excluir o fornecedor pois ele possui insumos vinculados");
        }

        fornecedorRepository.delete(fornecedor);
    }

    private Fornecedor buscarEntidadePorId(Long id) {
        Long veterinarioId = authService.getVeterinarioLogadoId();

        return fornecedorRepository.findByIdAndVeterinarioId(id, veterinarioId)
                .orElseThrow(() -> new NotFoundException("Fornecedor nao encontrado"));
    }

    private FornecedorResponseDTO toResponse(Fornecedor fornecedor) {
        return new FornecedorResponseDTO(
                fornecedor.getId(),
                fornecedor.getNome(),
                fornecedor.getContato(),
                fornecedor.getTelefone(),
                fornecedor.getEmail()
        );
    }
}
