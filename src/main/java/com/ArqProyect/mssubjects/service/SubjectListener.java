package com.ArqProyect.mssubjects.service;

import com.ArqProyect.mssubjects.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SubjectListener {

    @RabbitListener(queues = RabbitMQConfig.SUBJECTS_QUEUE)
    public void receiveMessage(String message) {
        System.out.println("Mensaje recibido: " + message);

        // Aquí puedes parsear el mensaje (JSON) a un objeto DTO
    }
}