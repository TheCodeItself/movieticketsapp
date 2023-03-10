package com.larasierra.movietickets.shopping.controller;

import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.validation.ValidId;
import com.larasierra.movietickets.shopping.application.OrderService;
import com.larasierra.movietickets.shopping.model.order.CreateOrderRequest;
import com.larasierra.movietickets.shopping.model.order.DefaultOrderResponse;
import com.larasierra.movietickets.shopping.model.order.InitOrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<InitOrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(request));
    }

    @GetMapping("/order/{id}")
    public DefaultOrderResponse findUserOrderById(@ValidId @PathVariable("id") String orderId) {
        return orderService.findUserOrderById(orderId)
                .orElseThrow(AppResourceNotFoundException::new);
    }

}
