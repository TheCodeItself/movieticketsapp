package com.larasierra.movietickets.moviemedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"com.larasierra.movietickets.moviemedia", "com.larasierra.movietickets.shared.controller"})
public class MovieMediaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieMediaServiceApplication.class, args);
    }

}
