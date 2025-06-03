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
        return new DirectExchange("inventory.exchange"); // Nombre del exchange
    }

    // Cola para la operación de CREAR PRODUCTO
    @Bean
    public Queue inventoryCreateQueue() {
        // La cola no necesita ser durable si no quieres que persista tras reinicios del broker
        // Si necesitas persistencia, cámbialo a true: new Queue("inventory.create.queue", true)
        return new Queue("inventory.create.queue", false);
    }

    // Binding para CREAR PRODUCTO: enlaza la cola 'inventoryCreateQueue' al 'inventoryExchange'
    // con la routing key 'inventory.create'
    @Bean
    public Binding inventoryCreateBinding(Queue inventoryCreateQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(inventoryCreateQueue).to(inventoryExchange).with("inventory.create");
    }

    // Cola para la operación de OBTENER PRODUCTO por ID
    @Bean
    public Queue inventoryGetQueue() {
        return new Queue("inventory.get.queue", false);
    }

    // Binding para OBTENER PRODUCTO: enlaza la cola 'inventoryGetQueue' al 'inventoryExchange'
    // con la routing key 'inventory.get'
    @Bean
    public Binding inventoryGetBinding(Queue inventoryGetQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(inventoryGetQueue).to(inventoryExchange).with("inventory.get");
    }

    // Puedes añadir más colas y bindings para otras operaciones (update, delete, etc.)
}