package com.larasierra.movietickets.movie.model.seat;

import java.time.OffsetDateTime;

public record FullSeatResponse(
        String seatId,
        String showtimeId,
        Boolean available,
        String purchaseToken,
        String orderId,
        OffsetDateTime createdAt
) {}
