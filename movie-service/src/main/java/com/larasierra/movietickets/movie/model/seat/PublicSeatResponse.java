package com.larasierra.movietickets.movie.model.seat;

import java.time.OffsetDateTime;

public record PublicSeatResponse(
        String seatId,
        String showtimeId,
        Boolean available,
        String purchaseToken
) {}
