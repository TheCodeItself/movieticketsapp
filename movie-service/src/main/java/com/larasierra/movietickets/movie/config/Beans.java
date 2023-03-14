package com.larasierra.movietickets.movie.config;

import com.larasierra.movietickets.shared.util.PurchaseTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Beans {

    @Bean
    public PurchaseTokenUtil purchaseTokenUtil(Environment environment) {
        return new PurchaseTokenUtil(environment);
    }

}
