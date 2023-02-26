package com.larasierra.movietickets.moviemedia.model.movie;

import java.time.OffsetDateTime;

public record MovieApiResponse(
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
