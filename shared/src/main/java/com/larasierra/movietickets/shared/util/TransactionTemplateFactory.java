package com.larasierra.movietickets.shared.util;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionTemplateFactory {
    private final PlatformTransactionManager platformTransactionManager;

    public TransactionTemplateFactory(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    public TransactionTemplate create() {
        return new TransactionTemplate(platformTransactionManager);
    }
}
