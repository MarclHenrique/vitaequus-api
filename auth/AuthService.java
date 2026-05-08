package com.reproequinos.vitaequus_api.auth;

import com.reproequinos.vitaequus_api.auth.dtos.CadastroRequest;
import com.reproequinos.vitaequus_api.auth.dtos.LoginRequest;
import com.reproequinos.vitaequus_api.auth.dtos.TokenResponse;
import com.reproequinos.vitaequus_api.auth.security.JwtUtil;
import com.reproequinos.vitaequus_api.entities.Veterinario;
import com.reproequinos.vitaequus_api.repositories.VeterinarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final VeterinarioRepository veterinarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(VeterinarioRepository veterinarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.veterinarioRepository = veterinarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public TokenResponse cadastrar(CadastroRequest request) {
        if (veterinarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        Veterinario veterinario = new Veterinario();
        veterinario.setNome(request.getNome());
        veterinario.setRegistroProfissional(request.getRegistroProfissional());
        veterinario.setTelefone(request.getTelefone());
        veterinario.setEmail(request.getEmail());
        // Senha sempre criptografada antes de persistir
        veterinario.setPassword(passwordEncoder.encode(request.getSenha()));

        veterinario = veterinarioRepository.save(veterinario);

        String token = jwtUtil.gerarToken(veterinario.getEmail(), veterinario.getId());
        return new TokenResponse(token, veterinario.getId(), veterinario.getNome(), veterinario.getEmail());
    }

    public TokenResponse login(LoginRequest request) {
        Veterinario veterinario = veterinarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "E-mail ou senha inválidos"
                ));

        if (!passwordEncoder.matches(request.getPassword(), veterinario.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "E-mail ou senha inválidos"
            );
        }

        String token = jwtUtil.gerarToken(veterinario.getEmail(), veterinario.getId());

        return new TokenResponse(
                token,
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getEmail()
        );
    }
}