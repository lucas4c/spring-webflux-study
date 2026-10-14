package com.drago.webflux_api.controller;

import com.drago.webflux_api.dto.UserRequestDTO;
import com.drago.webflux_api.dto.UserResponseDTO;
import com.drago.webflux_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Flux<UserResponseDTO> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // O @Valid aciona as validações criadas no UserRequestDTO
    public Mono<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return userService.save(userDTO);
    }
}