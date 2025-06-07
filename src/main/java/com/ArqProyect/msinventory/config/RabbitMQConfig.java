package com.ArqProyect.msinventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //Exchange central tipo TOPIC
    @Bean
    public TopicExchange apiGatewayExchange() {
        return new TopicExchange("apigateway.exchange");
    }

    //Una sola cola para msinventory
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable("msinventory.queue").build(); // durable = true
    }

    //Enlazar la cola al exchange con patrón topic
    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, TopicExchange apiGatewayExchange) {
        // Escucha todos los mensajes que empiezan con "inventory."
        return BindingBuilder.bind(inventoryQueue)
                .to(apiGatewayExchange)
                .with("inventory.#");
    }
}
