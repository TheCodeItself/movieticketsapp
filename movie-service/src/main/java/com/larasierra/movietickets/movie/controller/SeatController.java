package com.larasierra.movietickets.movie.controller;

import com.larasierra.movietickets.movie.application.SeatService;
import com.larasierra.movietickets.movie.model.seat.FullSeatResponse;
import com.larasierra.movietickets.movie.model.seat.RemovePurchaseTokenRequest;
import com.larasierra.movietickets.movie.model.seat.ReserveSeatForCartRequest;
import com.larasierra.movietickets.movie.model.seat.ReserveSeatForOrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/seat/{id}/remove-purchase-token")
    public ResponseEntity<Void> removePurchaseToken(
            @PathVariable("id") String seatId,
            @Valid @RequestBody RemovePurchaseTokenRequest request
    ) {
        seatService.removePurchaseToken(seatId, request);
        return ResponseEntity.noContent().build();
    }
}
