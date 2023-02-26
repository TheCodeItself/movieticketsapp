package com.larasierra.movietickets.movie.model.movie;

import java.time.OffsetDateTime;

public record DefaultMovieResponse(
        String movieId,
        String title,
        String country,
        String genre,
        Integer runtime,
        String rating,
        String synopsis,
        OffsetDateTime releaseDate,
        OffsetDateTime createdAt
) {}
