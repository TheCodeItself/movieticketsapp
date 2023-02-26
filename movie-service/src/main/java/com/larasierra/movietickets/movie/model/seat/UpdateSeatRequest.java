package com.larasierra.movietickets.movie.model.seat;

import jakarta.validation.constraints.NotNull;

public record UpdateSeatRequest(
        @NotNull
        Boolean available
) {}
