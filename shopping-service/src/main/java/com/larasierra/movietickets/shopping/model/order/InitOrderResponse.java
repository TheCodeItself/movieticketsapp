package com.larasierra.movietickets.shopping.model.order;

public record InitOrderResponse(
    String orderId,
    String clientSecret,
    String purchaseToken
) {}
