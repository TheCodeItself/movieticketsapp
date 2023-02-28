package com.larasierra.movietickets.shopping.model.cart;

import jakarta.validation.constraints.NotNull;

public record PurchaseTokenHolder(
        @NotNull
        String purchaseToken
) {}
