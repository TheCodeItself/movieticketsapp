package com.larasierra.movietickets.movie.model.theater;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTheaterRequest(
        @Size(min = 2, max = 250)
        @NotNull
        String theaterName,
        @Size(min = 2, max = 500)
        @NotNull
        String direction
) {}
