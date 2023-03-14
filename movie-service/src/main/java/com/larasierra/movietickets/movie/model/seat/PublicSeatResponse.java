package com.larasierra.movietickets.movie.model.seat;

import com.fasterxml.jackson.annotation.JsonInclude;

public record PublicSeatResponse(
        String seatNumber,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String showtimeId,
        Boolean available,
        String purchaseToken
) {}
