package com.larasierra.movietickets.user;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.larasierra.movietickets.user", "com.larasierra.movietickets.shared.controller"})
@OpenAPIDefinition(info =
    @Info(title = "User Service API", version = "1", description = "Documentation User API v1.0")
)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
