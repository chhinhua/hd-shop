package com.hdshop;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
public class HdShopApplication  {
	public static void main(String[] args) {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("message");
		messageSource.setDefaultEncoding("UTF-8");
		SpringApplication.run(HdShopApplication.class, args);
	}
}
