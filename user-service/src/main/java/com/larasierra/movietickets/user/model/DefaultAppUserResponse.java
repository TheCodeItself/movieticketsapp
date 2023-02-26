package com.larasierra.movietickets.user.model;

import java.time.OffsetDateTime;
import java.util.Set;

public record DefaultAppUserResponse (
        String userId,
        String email,
        Set<String> roles,
        OffsetDateTime createdAt
) {}
