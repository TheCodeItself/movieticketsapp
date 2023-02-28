package com.larasierra.movietickets.shopping.model.seat;

import jakarta.validation.constraints.NotNull;

public record RemovePurchaseTokenRequest(
        @NotNull
        String purchaseToken
) {}
