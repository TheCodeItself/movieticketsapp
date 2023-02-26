package com.larasierra.movietickets.movie.model.screen;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateScreenRequest(
        @ValidId
        @NotNull
        String theaterId,
        @Size(min = 1, max = 10)
        @NotNull
        String screenName,
        @Max(500L)
        @NotNull
        Short seatingCapacity
) {}
