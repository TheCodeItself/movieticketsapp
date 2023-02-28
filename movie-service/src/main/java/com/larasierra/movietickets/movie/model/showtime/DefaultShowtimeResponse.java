package com.larasierra.movietickets.movie.model.showtime;

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
