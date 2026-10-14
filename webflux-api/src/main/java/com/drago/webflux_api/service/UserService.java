package com.drago.webflux_api.service;

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

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> save(User user) {
        // Aqui você poderia adicionar regras de negócio, 
        // como verificar se o email já existe, antes de salvar.
        return userRepository.save(user);
    }
}