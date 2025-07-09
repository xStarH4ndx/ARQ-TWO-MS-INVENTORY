package com.ArqProyect.msinventory;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@EnableRabbit
@SpringBootApplication
public class MsInventoryApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		// Establece las variables como system env (opcional)
        System.setProperty("MONGODB_URI", dotenv.get("MONGODB_URI"));
        System.setProperty("RABBITMQ_URI", dotenv.get("RABBITMQ_URI"));
		SpringApplication.run(MsInventoryApplication.class, args);
	}

}
