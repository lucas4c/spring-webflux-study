package com.drago.webflux_api.service;

import com.drago.webflux_api.dto.UserRequestDTO;
import com.drago.webflux_api.dto.UserResponseDTO;
import com.drago.webflux_api.exception.UserNotFoundException;
import com.drago.webflux_api.model.User;
import com.drago.webflux_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<UserResponseDTO> findAll() {
        return userRepository.findAll().map(this::toResponseDTO);
    }

    public Mono<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id))) // Aqui lançamos o erro que será capturado pelo ExceptionHandler!
            .map(this::toResponseDTO);
    }

    public Mono<UserResponseDTO> save(UserRequestDTO dto) {
        User user = new User(dto.name(), dto.email());
        return userRepository.save(user).map(this::toResponseDTO);
    }

    // Métodos auxiliares de mapeamento (Abstração limpa)
    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }
}