package com.drago.webflux_api.service;

import com.drago.webflux_api.config.KafkaConfig;
import com.drago.webflux_api.dto.UserRequestDTO;
import com.drago.webflux_api.dto.UserResponseDTO;
import com.drago.webflux_api.exception.UserNotFoundException;
import com.drago.webflux_api.model.User;
import com.drago.webflux_api.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public Flux<UserResponseDTO> findAll() {
        return userRepository.findAll().map(this::toResponseDTO);
    }

    public Mono<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id))) // Aqui lançamos o erro que será capturado pelo ExceptionHandler!
            .map(this::toResponseDTO);
    }

    public Mono<UserResponseDTO> save(UserRequestDTO requestDTO) {
    User user = new User(requestDTO.name(), requestDTO.email(), passwordEncoder.encode(requestDTO.password()));

    return userRepository.save(user)
        .map(savedUser -> {
            UserResponseDTO response = new UserResponseDTO(
                savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getCreatedAt()
            );

            // Tenta converter o DTO em JSON e envia para o Kafka de forma assíncrona (fire-and-forget)
            try {
                String eventPayload = objectMapper.writeValueAsString(response);
                kafkaTemplate.send(KafkaConfig.USER_CREATED_TOPIC, savedUser.getEmail(), eventPayload);
            } catch (Exception e) {
                // Em produção, isso iria para um log ou Dead Letter Queue, mas não deve travar a criação do usuário
                System.err.println("Erro ao publicar evento no Kafka: " + e.getMessage());
            }

            return response;
        });
}

    // Métodos auxiliares de mapeamento (Abstração limpa)
    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }
}