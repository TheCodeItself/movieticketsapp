package com.larasierra.movietickets.movie.model.seat;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.constraints.NotNull;

public record CreateSeatsForShowtime(
    @ValidId
    @NotNull
    String showtimeId
) {}
