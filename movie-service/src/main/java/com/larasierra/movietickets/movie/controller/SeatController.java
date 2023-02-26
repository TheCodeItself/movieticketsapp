package com.larasierra.movietickets.movie.controller;

import com.larasierra.movietickets.movie.application.SeatService;
import com.larasierra.movietickets.movie.model.seat.FullSeatResponse;
import com.larasierra.movietickets.movie.model.seat.ReserveSeatForCartRequest;
import com.larasierra.movietickets.movie.model.seat.ReserveSeatForOrderRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping("/seat/{id}/reserve-for-cart")
    public void reserveForCart(
            @PathVariable("id") String seatId,
            @Valid @RequestBody ReserveSeatForCartRequest request
    ) {
        seatService.reserveForCart(seatId, request);
    }

    @PostMapping("/seat/reserve-for-order")
    public List<FullSeatResponse> reserveForOrder(@Valid @RequestBody ReserveSeatForOrderRequest request) {
        return seatService.reserveForOrder(request);
    }
}
