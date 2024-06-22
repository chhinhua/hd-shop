package com.duck.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HD-Shop Application API")
                        .description("Fashion shop RESTful API documentation")
                        .contact(new Contact()
                                .email("chhinhua.com")
                                .name("Chhin Hua")
                                //.url("https://www.linkedin.com/in/chhin-hua/")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")
                        )
                        .version("v1.0")
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Github repository")
                        .url("https://github.com/chhinhua/hd-shop")
                );
    }
}