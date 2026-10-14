package com.drago.webflux_api.service;

import com.drago.webflux_api.dto.AuthRequestDTO;
import com.drago.webflux_api.dto.AuthResponseDTO;
import com.drago.webflux_api.repository.UserRepository;
import com.drago.webflux_api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Mono<AuthResponseDTO> login(AuthRequestDTO request) {
        return userRepository.findByEmail(request.email())
            .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
            .map(user -> new AuthResponseDTO(jwtService.generateToken(user.getEmail(), user.getRole())))
            .switchIfEmpty(Mono.error(new RuntimeException("Credenciais inválidas"))); // Aqui vc pode criar uma CustomException
    }
}