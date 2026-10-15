package com.drago.notification_service.listener;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final ObjectMapper objectMapper;

    public NotificationListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user.created.topic", groupId = "notification-group")
    public void consumeUserCreatedEvent(String message) {
        try {
            // Lendo o JSON recebido como String pura
            JsonNode event = objectMapper.readTree(message);
            String name = event.get("name").asText();
            String email = event.get("email").asText();

            System.out.println("==================================================");
            System.out.println("🚀 [NOTIFICATION SERVICE] Evento Kafka Consumido!");
            System.out.println("📧 Simulando envio de email de boas-vindas...");
            System.out.println("👤 Nome: " + name);
            System.out.println("✉️ Para: " + email);
            System.out.println("✅ Status: Email enviado com sucesso!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem do Kafka: " + e.getMessage());
        }
    }
}