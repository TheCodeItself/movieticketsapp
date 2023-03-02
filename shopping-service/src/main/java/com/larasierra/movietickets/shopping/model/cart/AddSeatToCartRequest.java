package com.larasierra.movietickets.shopping.model.cart;

import jakarta.validation.constraints.NotNull;

public record AddSeatToCartRequest(
   @NotNull
   String seatId,
   @NotNull
   String ticketType,
   String seatToken,
   @NotNull
   String purchaseToken
) {}
