package com.larasierra.movietickets.shopping.model.seat;

import java.time.OffsetDateTime;

public record ReserveSeatForOrderResponse(
        String seatId,
        String showtimeId,
        Boolean available,
        String purchaseToken,
        String orderId,
        OffsetDateTime createdAt
) {}
