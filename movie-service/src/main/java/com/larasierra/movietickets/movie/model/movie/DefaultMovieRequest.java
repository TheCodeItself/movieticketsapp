package com.larasierra.movietickets.movie.model.movie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record DefaultMovieRequest(
        @Size(min = 2, max = 1000)
        @NotNull
        String title,

        @Size(min = 2, max = 250)
        @NotNull
        String country,

        @Size(min = 2, max = 500)
        @NotNull
        String genre,

        @NotNull
        Integer runtime,

        @Size(min = 2, max = 100)
        @NotNull
        String rating,

        @Size(max = 2000)
        @NotNull
        String synopsis,

        @NotNull
        OffsetDateTime releaseDate
) {}
