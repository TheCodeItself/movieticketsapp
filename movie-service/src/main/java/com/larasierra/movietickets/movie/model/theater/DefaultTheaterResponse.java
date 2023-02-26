package com.larasierra.movietickets.movie.model.theater;

import java.time.OffsetDateTime;

public record DefaultTheaterResponse(
        String theaterId,
        String theaterName,
        String city,
        String direction,
        OffsetDateTime createdAt
) {}
