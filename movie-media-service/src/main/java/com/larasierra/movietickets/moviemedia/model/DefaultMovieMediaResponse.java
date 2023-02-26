package com.larasierra.movietickets.moviemedia.model;

import java.time.OffsetDateTime;

public record DefaultMovieMediaResponse(
        String movieMediaId,
        String movieId,
        String url,
        String filename,
        String contentType,
        Long fileLength,
        String mediaRole,
        OffsetDateTime createdAt
) {}
