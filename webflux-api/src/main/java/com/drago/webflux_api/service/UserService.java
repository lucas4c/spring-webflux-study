package com.drago.webflux_api.service;

import com.drago.webflux_api.config.RabbitMQConfig;
import com.drago.webflux_api.dto.UserRequestDTO;
import com.drago.webflux_api.dto.UserResponseDTO;
import com.drago.webflux_api.exception.UserNotFoundException;
import com.drago.webflux_api.model.User;
import com.drago.webflux_api.repository.UserRepository;

import io.jsonwebtoken.security.Password;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
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
        User user = new User(
            requestDTO.name(),
            requestDTO.email(),
            passwordEncoder.encode(requestDTO.password())
        );

        return userRepository.save(user)
            .map(savedUser -> {
                // Publica o evento no RabbitMQ
                String message = String.format("{\"email\":\"%s\",\"name\":\"%s\"}", savedUser.getEmail(), savedUser.getName());
                rabbitTemplate.convertAndSend(RabbitMQConfig.USER_CREATED_QUEUE, message);
                
                return new UserResponseDTO(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getCreatedAt()
                );
            });
    }

    // Métodos auxiliares de mapeamento (Abstração limpa)
    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }
}