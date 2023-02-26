package com.larasierra.movietickets.movie.model.showtime;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record DefaultShowtimeResponse(
        String showtimeId,
        String screenId,
        String theaterId,
        String movieId,
        OffsetDateTime startDate,
        Integer standardPrice,
        Integer preferentialPrice,
        OffsetDateTime createdAt
) { }
