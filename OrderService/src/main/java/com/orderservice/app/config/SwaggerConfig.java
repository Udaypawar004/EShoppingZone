package com.orderservice.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service - E-Shopping Zone API")
                        .description("Handles order placement, status, and customer-related order info.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Uday Deshmukh")
                                .email("udaypawar004@gmail.com")
                                .url("https://github.com/udaypawar004"))
                );
    }
}
