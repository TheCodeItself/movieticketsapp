package com.larasierra.movietickets.movie.model.screen;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record DefaultScreenResponse(
        String screenId,
        String theaterId,
        String screenName,
        Short seatingCapacity,
        OffsetDateTime createdAt
) {}
