package com.larasierra.movietickets.shopping.model.seat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReserveSeatForOrderRequest(
      @NotEmpty
      List<String> seats,
      @NotNull
      String userToken
) {}
