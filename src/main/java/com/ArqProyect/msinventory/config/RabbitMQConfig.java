package com.ArqProyect.msinventory.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable("msinventory.queue").build(); // durable = true
    }

    @Bean
    public Queue gastoCompraQueue() {
        return QueueBuilder.durable("gastoCompra.queue").build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
