package com.larasierra.movietickets.shopping.controller;

import com.larasierra.movietickets.shopping.application.ShoppingCartService;
import com.larasierra.movietickets.shopping.model.cart.AddSeatToCartRequest;
import com.larasierra.movietickets.shopping.model.cart.PurchaseTokenHolder;
import com.larasierra.movietickets.shopping.model.cart.ShoppingCartItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/cart/purchase-token")
    public PurchaseTokenHolder generatePurchaseToken() {
        return new PurchaseTokenHolder(shoppingCartService.generatePurchaseToken());
    }

    @PostMapping("/cart/item")
    public void addSeatToCart(@Valid @RequestBody AddSeatToCartRequest request) {
        shoppingCartService.addSeatToCart(request);
    }

    @DeleteMapping("/cart/item/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("id") String seatId,
            @Valid @RequestBody PurchaseTokenHolder request
    ) {
        shoppingCartService.removeItem(seatId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart/item")
    public List<ShoppingCartItemResponse> findAllUserItems() {
        return shoppingCartService.findAllUserItems();
    }

}
