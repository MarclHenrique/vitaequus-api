package com.reproequinos.vitaequus_api.services;

import com.reproequinos.vitaequus_api.Dto.Request.AlterarSenhaRequestDTO;
import com.reproequinos.vitaequus_api.Dto.Request.VeterinarioPerfilUpdateDTO;
import com.reproequinos.vitaequus_api.Dto.Response.VeterinarioPerfilResponseDTO;
import com.reproequinos.vitaequus_api.auth.AuthService;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.exceptions.BadRequestException;
import com.reproequinos.vitaequus_api.repositories.VeterinarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;

    public VeterinarioService(
            VeterinarioRepository veterinarioRepository,
            AuthService authService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.veterinarioRepository = veterinarioRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public VeterinarioPerfilResponseDTO buscarPerfilLogado() {
        return toPerfilResponse(authService.getVeterinarioLogado());
    }

    @Transactional
    public VeterinarioPerfilResponseDTO atualizarPerfilLogado(VeterinarioPerfilUpdateDTO dto) {
        Veterinario veterinario = authService.getVeterinarioLogado();

        if (veterinarioRepository.existsByEmailAndIdNot(dto.email(), veterinario.getId())) {
            throw new BadRequestException("E-mail ja cadastrado");
        }

        veterinario.setNome(dto.nome());
        veterinario.setTelefone(dto.telefone());
        veterinario.setEmail(dto.email());
        veterinario.setBaseCidade(dto.baseCidade());

        return toPerfilResponse(veterinario);
    }

    @Transactional
    public void alterarSenha(AlterarSenhaRequestDTO dto) {
        Veterinario veterinario = authService.getVeterinarioLogado();

        if (!passwordEncoder.matches(dto.senhaAtual(), veterinario.getPassword())) {
            throw new BadRequestException("Senha atual invalida");
        }

        veterinario.setPassword(passwordEncoder.encode(dto.novaSenha()));
    }

    private VeterinarioPerfilResponseDTO toPerfilResponse(Veterinario veterinario) {
        return new VeterinarioPerfilResponseDTO(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getRegistroProfissional(),
                veterinario.getTelefone(),
                veterinario.getEmail(),
                veterinario.getBaseCidade(),
                veterinario.getRole()
        );
    }
}
