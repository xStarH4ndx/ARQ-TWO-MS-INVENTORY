package com.ArqProyect.msinventory.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Exchange inventoryExchange() {
        return new FanoutExchange("invetory.exchange");
    }

    
}
