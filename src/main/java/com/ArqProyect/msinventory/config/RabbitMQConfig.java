package com.ArqProyect.msinventory.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable("msinventory.queue").build(); // durable = true
    }
}
