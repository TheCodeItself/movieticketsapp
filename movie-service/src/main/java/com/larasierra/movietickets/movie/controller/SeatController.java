package com.larasierra.movietickets.movie.controller;

import com.larasierra.movietickets.movie.application.SeatService;
import com.larasierra.movietickets.movie.model.seat.*;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.validation.ValidId;
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

    @GetMapping("/seat/{id}")
    public PublicSeatResponse findById(@PathVariable("id") String seatId) {
        return seatService.findById(seatId)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @GetMapping("/seat")
    public List<PublicSeatResponse> findAllByShowtimeId(@ValidId @RequestParam String showtimeId) {
        return seatService.findAllByShowtimeId(showtimeId);
    }
}
