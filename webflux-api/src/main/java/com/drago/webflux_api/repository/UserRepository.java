package com.drago.webflux_api.repository;

import com.drago.webflux_api.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    
    // Exemplo de método customizado (o Spring implementa automaticamente)
    Mono<User> findByEmail(String email);
}