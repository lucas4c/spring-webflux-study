package com.drago.webflux_api.controller;

import com.drago.webflux_api.dto.AuthRequestDTO;
import com.drago.webflux_api.dto.AuthResponseDTO;
import com.drago.webflux_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public Mono<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return authService.login(request);
    }
}