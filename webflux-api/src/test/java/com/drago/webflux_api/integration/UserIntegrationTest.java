package com.drago.webflux_api.integration;

import com.drago.webflux_api.dto.UserRequestDTO;
import com.drago.webflux_api.dto.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// Sobe a aplicação completa em uma porta aleatória
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserIntegrationTest {

    // Declara o container do PostgreSQL que será gerenciado pelo Testcontainers
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // Injeta dinamicamente as propriedades do container R2DBC no Spring Boot
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Converte a URL jdbc do Testcontainers para o formato r2dbc:postgresql
        registry.add("spring.r2dbc.url", () -> 
            String.format("r2dbc:postgresql://%s:%d/%s",
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName()
            )
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Fluxo de Integração: Deve criar e persistir o usuário no PostgreSQL real")
    void shouldCreateUserInRealDatabase() {
        UserRequestDTO request = new UserRequestDTO("Carlos Integração", "carlos@test.com", "senha123");

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .consumeWith(response -> {
                    UserResponseDTO body = response.getResponseBody();
                    assert body != null;
                    assert body.name().equals("Carlos Integração");
                    assert body.email().equals("carlos@test.com");
                });
    }
}