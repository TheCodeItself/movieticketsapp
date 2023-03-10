package com.larasierra.movietickets.shopping.model.order;

import java.time.OffsetDateTime;
import java.util.List;

public record DefaultOrderResponse(
        String orderId,
        String userId,
        String purchaseToken,
        String status,
        Boolean cancel,
        Long totalCents,
        OffsetDateTime createdAt,
        List<DefaultOrderItemResponse> items
) {}
