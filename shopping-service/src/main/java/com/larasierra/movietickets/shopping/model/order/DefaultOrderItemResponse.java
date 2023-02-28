package com.larasierra.movietickets.shopping.model.order;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record DefaultOrderItemResponse(
        String orderItemId,
        String orderId,
        String userId,
        String seatId,
        String ticketType,
        Integer priceCents,
        OffsetDateTime createdAt
) {}
