package com.drago.webflux_api.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Usuário com ID " + id + " não foi encontrado.");
    }
}