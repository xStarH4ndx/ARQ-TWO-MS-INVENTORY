package com.ArqProyect.msinventory;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class MsInventoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsInventoryApplication.class, args);
	}

}
