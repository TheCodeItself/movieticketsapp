package com.larasierra.movietickets.shopping.model.seat;

import jakarta.validation.constraints.NotNull;

public record ReserveSeatForCartApiRequest(
   String seatToken,
   @NotNull
   String purchaseToken
) {}
