package com.larasierra.movietickets.moviemedia.model;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.constraints.NotNull;

public record CreateMovieMediaRequest(
        @ValidId
        @NotNull
        String movieId,
        @NotNull
        String mediaRole
) {}
