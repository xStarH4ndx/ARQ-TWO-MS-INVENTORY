package com.ArqProyect.msinventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // definicion del exchange
    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange("inventory.exchange");
    }

    // Conexion con apigateway
    @Bean
    public Queue apiToInventoryQueue() {
        return new Queue("api_to_inventory_queue", false); // Nombre de la cola y durabilidad
    }

    // Binding que enlaza la cola 'apiToInventoryQueue' al exchange 'inventoryExchange'
    // con una clave de enrutamiento específica (ejemplo: 'inventory.create')
    @Bean
    public Binding apiInventoryBinding(Queue apiToInventoryQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(apiToInventoryQueue).to(inventoryExchange()).with("inventory.create");
    }
}