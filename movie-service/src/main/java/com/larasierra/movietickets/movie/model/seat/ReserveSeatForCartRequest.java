package com.larasierra.movietickets.movie.model.seat;

import jakarta.validation.constraints.NotNull;

public record ReserveSeatForCartRequest(
   String seatToken,
   @NotNull
   String purchaseToken
) {}
