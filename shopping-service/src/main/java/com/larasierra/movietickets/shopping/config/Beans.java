package com.larasierra.movietickets.shopping.config;

import com.larasierra.movietickets.shared.util.PurchaseTokenUtil;
import com.larasierra.movietickets.shared.util.TransactionTemplateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Beans {

    @Bean
    public PurchaseTokenUtil purchaseTokenUtil(Environment environment) {
        return new PurchaseTokenUtil(environment);
    }

    @Bean
    public TransactionTemplateFactory transactionTemplateFactory(PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplateFactory(platformTransactionManager);
    }

}
