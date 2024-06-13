package com.hdshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DuckShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(DuckShopApplication.class, args);
	}
}
