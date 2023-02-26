package com.larasierra.movietickets.movie.model.showtime;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record UpdateShowtimeRequest(
        @ValidId
        @NotNull
        String screenId,
        @NotNull
        OffsetDateTime startDate,
        @Min(100)
        @NotNull
        Integer standardPrice,
        @Min(100)
        @NotNull
        Integer preferentialPrice
) {}
