package com.larasierra.movietickets.shopping.external.apiclient;

import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForCartApiRequest;
import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForOrderRequest;
import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "movie-service")
public interface SeatApiClient {

    @PostMapping("/seat/{id}/reserve-for-cart")
    void reserveForCart(@PathVariable("id") String seatId, @RequestBody ReserveSeatForCartApiRequest request);

    @PostMapping("/seat/reserve-for-order")
    List<ReserveSeatForOrderResponse> reserveForOrder(@RequestBody ReserveSeatForOrderRequest request);
}
