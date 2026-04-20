package com.reproequinos.vitaequus_api.auth;

import com.reproequinos.vitaequus_api.auth.dtos.CadastroRequest;
import com.reproequinos.vitaequus_api.auth.dtos.LoginRequest;
import com.reproequinos.vitaequus_api.auth.dtos.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /auth/cadastro
     * Cria um novo veterinário e retorna o token JWT.
     */
    @PostMapping("/cadastro")
    public ResponseEntity<TokenResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        TokenResponse response = authService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /auth/login
     * Autentica o veterinário e retorna o token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}