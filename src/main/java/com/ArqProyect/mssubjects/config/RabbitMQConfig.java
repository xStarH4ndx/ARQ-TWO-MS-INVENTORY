package com.ArqProyect.mssubjects.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SUBJECTS_QUEUE = "subjects-queue";

    @Bean
    public Queue subjectsQueue() {
        return new Queue(SUBJECTS_QUEUE, false);
    }
}
