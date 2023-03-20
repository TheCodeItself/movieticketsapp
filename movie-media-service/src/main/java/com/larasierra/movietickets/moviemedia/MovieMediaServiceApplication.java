package com.larasierra.movietickets.moviemedia;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"com.larasierra.movietickets.moviemedia", "com.larasierra.movietickets.shared.controller"})
@OpenAPIDefinition(info =
    @Info(title = "Movie Media Service API", version = "1", description = "Documentation Movie Media API v1.0")
)
public class MovieMediaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieMediaServiceApplication.class, args);
    }

}
