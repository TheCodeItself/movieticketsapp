package com.larasierra.movietickets.shopping.model.cart;

public record ShoppingCartItemResponse (
        String userId,
        String seatId,
        String ticketType
) { }
