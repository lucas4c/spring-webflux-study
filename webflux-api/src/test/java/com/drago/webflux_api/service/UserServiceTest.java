package com.drago.webflux_api.service;

import com.drago.webflux_api.dto.UserResponseDTO;
import com.drago.webflux_api.exception.UserNotFoundException;
import com.drago.webflux_api.model.User;
import com.drago.webflux_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

// ExtendWith diz ao JUnit para processar as anotações @Mock e @InjectMocks
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // O que queremos "fingir" (mock) que existe
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // A classe real que vamos testar, injetando os mocks acima
    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Prepara um usuário fictício antes de cada teste
        mockUser = new User("Lucas", "lucas@example.com", "senha-hash");
        mockUser.setId(1L);
        mockUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve retornar o usuário com sucesso quando o ID existir")
    void findById_Success() {
        // Configura o comportamento do Mock (Quando buscar o ID 1, retorne o mockUser)
        when(userRepository.findById(1L)).thenReturn(Mono.just(mockUser));

        // Executa o método real
        Mono<UserResponseDTO> result = userService.findById(1L);

        // Usa o StepVerifier para validar o fluxo reativo
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.name().equals("Lucas") && 
                    dto.email().equals("lucas@example.com")
                )
                .verifyComplete(); // Garante que o fluxo terminou sem erros
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o ID não existir")
    void findById_NotFound() {
        // Quando buscar qualquer ID, retorne Mono.empty() (vazio)
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        // Executa o método real
        Mono<UserResponseDTO> result = userService.findById(99L);

        // Verifica se o erro foi lançado corretamente pelo switchIfEmpty do UserService
        StepVerifier.create(result)
                .expectError(UserNotFoundException.class)
                .verify(); // Verifica se estourou o erro esperado
    }
}