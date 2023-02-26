package com.larasierra.movietickets.movie.model.showtime;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CreateShowtimeRequest (
        @ValidId
        @NotNull
        String screenId,
        @ValidId
        @NotNull
        String theaterId,
        @ValidId
        @NotNull
        String movieId,
        @NotNull
        OffsetDateTime startDate,
        @Min(100)
        @NotNull
        Integer standardPrice,
        @Min(100)
        @NotNull
        Integer preferentialPrice
) {}
