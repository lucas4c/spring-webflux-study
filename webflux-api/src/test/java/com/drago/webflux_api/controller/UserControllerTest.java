package com.drago.webflux_api.controller;

import com.drago.webflux_api.dto.UserRequestDTO;
import com.drago.webflux_api.dto.UserResponseDTO;
import com.drago.webflux_api.security.JwtAuthenticationFilter;
import com.drago.webflux_api.security.JwtService;
import com.drago.webflux_api.security.SecurityConfig;
import com.drago.webflux_api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    // Mock necessário para o RateLimitFilter não quebrar o contexto web isolado
    @MockitoBean
    private ReactiveStringRedisTemplate redisTemplate;

    @Test
    @DisplayName("POST /api/users - Deve criar um usuário com sucesso (Rota Pública)")
    void createUser_Success() {
        UserRequestDTO request = new UserRequestDTO("Lucas", "lucas@test.com", "123456");
        UserResponseDTO response = new UserResponseDTO(1L, "Lucas", "lucas@test.com", LocalDateTime.now());

        when(userService.save(any(UserRequestDTO.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Lucas")
                .jsonPath("$.email").isEqualTo("lucas@test.com");
    }

    @Test
    @DisplayName("GET /api/users - Deve listar usuários quando autenticado")
    @WithMockUser(roles = "USER")
    void getAllUsers_Success() {
        UserResponseDTO user1 = new UserResponseDTO(1L, "Lucas", "lucas@test.com", LocalDateTime.now());
        UserResponseDTO user2 = new UserResponseDTO(2L, "Admin", "admin@test.com", LocalDateTime.now());

        when(userService.findAll()).thenReturn(Flux.just(user1, user2));

        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDTO.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("GET /api/users - Deve retornar 401 Unauthorized sem token")
    void getAllUsers_Unauthorized() {
        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}