package com.larasierra.movietickets.shopping;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.larasierra.movietickets.shopping", "com.larasierra.movietickets.shared.controller"})
@OpenAPIDefinition(info =
    @Info(title = "Shopping Service API", version = "1", description = "Documentation Shopping API v1.0")
)
public class ShoppingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingServiceApplication.class, args);
    }

}
