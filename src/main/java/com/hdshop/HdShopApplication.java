package com.hdshop;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HdShopApplication  {
	@Bean
	public Dotenv dotenv() {
		return Dotenv.load();
	}

	public static void main(String[] args) {
		SpringApplication.run(HdShopApplication.class, args);
	}
}
