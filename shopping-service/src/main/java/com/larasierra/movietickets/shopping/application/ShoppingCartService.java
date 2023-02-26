package com.larasierra.movietickets.shopping.application;

import com.larasierra.movietickets.shared.exception.AppResourceLockedException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shopping.domain.ShoppingCartItem;
import com.larasierra.movietickets.shopping.domain.ShoppingCartItemPk;
import com.larasierra.movietickets.shopping.external.apiclient.SeatApiClient;
import com.larasierra.movietickets.shopping.external.jpa.ShoppingCartItemRepository;
import com.larasierra.movietickets.shopping.model.cart.AddSeatToCartRequest;
import com.larasierra.movietickets.shopping.model.cart.ShoppingCartItemResponse;
import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForCartApiRequest;
import feign.FeignException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final AuthInfo authInfo;
    private final SeatApiClient seatApiClient;

    public ShoppingCartService(ShoppingCartItemRepository shoppingCartItemRepository, AuthInfo authInfo, SeatApiClient seatApiClient) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.authInfo = authInfo;
        this.seatApiClient = seatApiClient;
    }

    @PreAuthorize("hasRole('enduser')")
    public String generatePurchaseToken(String showtimeId) {
        // only the user who requested the token can use it to add a seat to the cart or place an order

        // create new token: time + random + hash(showtimeId:time) using user's random
        // delete existing cart items???
        return null;
    }

    @PreAuthorize("hasRole('enduser')")
    public void addSeatToCart(AddSeatToCartRequest request) {
        // TODO: 26/02/2023 validate purchase token

        try {
            var reserveSeatForCartApiRequest = new ReserveSeatForCartApiRequest(request.seatToken(), request.purchaseToken());
            seatApiClient.reserveForCart(request.seatId(), reserveSeatForCartApiRequest);
        } catch (FeignException.Conflict conflict) {
            throw new AppResourceLockedException();
        }

        var shoppingCartItemPk = new ShoppingCartItemPk(authInfo.userId(), request.seatId());
        var cartItem = new ShoppingCartItem(shoppingCartItemPk, request.ticketType());
        shoppingCartItemRepository.save(cartItem);
    }

    @PreAuthorize("hasRole('enduser')")
    public List<ShoppingCartItemResponse> findAllUserItems() {
        return shoppingCartItemRepository.findAllByShoppingCartItemPk_UserId(authInfo.userId())
                .stream()
                .map(this::toDefaultResponse)
                .toList();
    }

    private ShoppingCartItemResponse toDefaultResponse(ShoppingCartItem item) {
        return new ShoppingCartItemResponse(
                item.getShoppingCartItemPk().getUserId(),
                item.getShoppingCartItemPk().getSeatId(),
                item.getTicketType()
        );
    }
}
