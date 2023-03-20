package com.larasierra.movietickets.movie;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.larasierra.movietickets.movie", "com.larasierra.movietickets.shared.controller"})
@OpenAPIDefinition(info =
    @Info(title = "Movie Service API", version = "1", description = "Documentation Movie API v1.0")
)
public class MovieServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieServiceApplication.class, args);
    }

}
