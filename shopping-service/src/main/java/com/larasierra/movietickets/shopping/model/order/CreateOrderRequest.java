package com.larasierra.movietickets.shopping.model.order;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest (
        @NotNull
        String purchaseToken
) {}
