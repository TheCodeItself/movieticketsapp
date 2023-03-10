package com.larasierra.movietickets.shopping.controller;

import com.larasierra.movietickets.shopping.application.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class StripeWebhookController {
    private final OrderService orderService;

    public StripeWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/stripe/webhook")
    public void webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        orderService.processPaymentEvent(payload, sigHeader);
    }
}
