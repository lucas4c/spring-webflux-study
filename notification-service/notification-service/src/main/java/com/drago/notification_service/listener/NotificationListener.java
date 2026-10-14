package main.java.com.drago.notification_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @RabbitListener(queues = "user.created.queue")
    public void consumeUserCreatedEvent(String message) {
        System.out.println("--------------------------------------------------");
        System.out.println("[NOTIFICATION SERVICE] Evento recebido do RabbitMQ!");
        System.out.println("Enviando email de boas-vindas para: " + message);
        System.out.println("Status: Email enviado com sucesso! ✉️");
        System.out.println("--------------------------------------------------");
    }
}