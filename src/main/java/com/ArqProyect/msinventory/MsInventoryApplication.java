package com.ArqProyect.msinventory;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class MsInventoryApplication {

	public static void main(String[] args) {
		System.out.println("MONGODB_URI = " + System.getenv("MONGODB_URI"));
		System.out.println("RABBITMQ_URI = " + System.getenv("RABBITMQ_URI"));
		SpringApplication.run(MsInventoryApplication.class, args);
	}

}
