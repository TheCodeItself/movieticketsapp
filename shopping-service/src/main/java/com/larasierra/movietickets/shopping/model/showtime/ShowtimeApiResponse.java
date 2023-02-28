package com.larasierra.movietickets.shopping.model.showtime;

import java.time.OffsetDateTime;

public record ShowtimeApiResponse(
        String showtimeId,
        String screenId,
        String theaterId,
        String movieId,
        OffsetDateTime startDate,
        Integer standardPrice,
        Integer preferentialPrice,
        OffsetDateTime createdAt
) { }
