package com.ArqProyect.msinventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Definición del Direct Exchange que usará la API Gateway
    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange("apigateway.exchange"); // Nombre del exchange
    }

    // Cola para la operación de CREAR PRODUCTO
    @Bean
    public Queue inventoryCreateQueue() {
        return new Queue("producto.create.queue", false);
    }

    @Bean
    public Binding inventoryCreateBinding(Queue inventoryCreateQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(inventoryCreateQueue)
        .to(inventoryExchange)
        .with("producto.create");
    }

    // Cola para la operación de OBTENER PRODUCTO por ID
    @Bean
    public Queue inventoryGetQueue() {
        return new Queue("producto.get.queue", false);
    }

    @Bean
    public Binding inventoryGetBinding(Queue inventoryGetQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(inventoryGetQueue)
        .to(inventoryExchange)
        .with("producto.get");
    }

    // Cola para realizar una COMPRA
    @Bean
    public Queue inventoryCompraQueue() {
        return new Queue("compra.create.queue", false);
    }

    @Bean
    public Binding inventoryCompraBinding(Queue inventoryCompraQueue, DirectExchange apigatewayExchange) {
        return BindingBuilder.bind(inventoryCompraQueue)
            .to(apigatewayExchange)
            .with("compra.create");
    }
}