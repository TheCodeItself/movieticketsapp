package com.larasierra.movietickets.shopping.application;

import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppResourceLockedException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.IdUtil;
import com.larasierra.movietickets.shared.util.PurchaseTokenUtil;
import com.larasierra.movietickets.shopping.domain.ShoppingCartItem;
import com.larasierra.movietickets.shopping.domain.ShoppingCartItemPk;
import com.larasierra.movietickets.shopping.external.apiclient.SeatApiClient;
import com.larasierra.movietickets.shopping.external.apiclient.ShowtimeApiClient;
import com.larasierra.movietickets.shopping.external.jpa.ShoppingCartItemRepository;
import com.larasierra.movietickets.shopping.model.cart.AddSeatToCartRequest;
import com.larasierra.movietickets.shopping.model.cart.PurchaseTokenHolder;
import com.larasierra.movietickets.shopping.model.cart.ShoppingCartItemResponse;
import com.larasierra.movietickets.shopping.model.seat.RemovePurchaseTokenRequest;
import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForCartApiRequest;
import com.larasierra.movietickets.shopping.model.showtime.ShowtimeApiResponse;
import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final AuthInfo authInfo;
    private final SeatApiClient seatApiClient;
    private final ShowtimeApiClient showtimeApiClient;
    private final PurchaseTokenUtil purchaseTokenUtil;

    public ShoppingCartService(ShoppingCartItemRepository shoppingCartItemRepository, AuthInfo authInfo, SeatApiClient seatApiClient, ShowtimeApiClient showtimeApiClient, PurchaseTokenUtil purchaseTokenUtil) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.authInfo = authInfo;
        this.seatApiClient = seatApiClient;
        this.showtimeApiClient = showtimeApiClient;
        this.purchaseTokenUtil = purchaseTokenUtil;
    }

    @PreAuthorize("hasRole('enduser')")
    public String generatePurchaseToken() {
        String timeSortedId = IdUtil.next();

        // only the user who requested the token can use it to add a seat to the cart or place an order
        String token = purchaseTokenUtil.generateToken(timeSortedId, authInfo.userId());

        // delete existing cart items
        shoppingCartItemRepository.deleteByUserId(authInfo.userId());

        return token;
    }

    @PreAuthorize("hasRole('enduser')")
    public void addSeatToCart(AddSeatToCartRequest request) {
        purchaseTokenUtil.validateToken(authInfo.userId(), request.purchaseToken());

        if (purchaseTokenUtil.isTokenExpired(request.purchaseToken())) {
            throw new AppBadRequestException("the token has expired");
        }

        try {
            var reserveSeatForCartApiRequest = new ReserveSeatForCartApiRequest(request.seatToken(), request.purchaseToken());
            seatApiClient.reserveForCart(request.seatId(), reserveSeatForCartApiRequest);
        } catch (FeignException.Conflict conflict) {
            throw new AppResourceLockedException();
        }

        String showtimeId = extractShowtimeIdFromSeatId(request.seatId());
        ShowtimeApiResponse showtime = showtimeApiClient.findById(showtimeId);


        var shoppingCartItemPk = new ShoppingCartItemPk(authInfo.userId(), request.seatId());
        int priceCents = request.ticketType().equals("s") ? showtime.standardPrice()
                                                          : showtime.preferentialPrice();
        var cartItem = ShoppingCartItem.builder()
                .shoppingCartItemPk(shoppingCartItemPk)
                .ticketType(request.ticketType())
                .priceCents(priceCents)
                .build();

        shoppingCartItemRepository.save(cartItem);
    }

    @PreAuthorize("hasRole('enduser')")
    public void removeItem(String seatId, PurchaseTokenHolder request) {
        shoppingCartItemRepository.deleteById(new ShoppingCartItemPk(authInfo.userId(), seatId));
        seatApiClient.removePurchaseToken(seatId, new RemovePurchaseTokenRequest(request.purchaseToken()));
    }

    @PreAuthorize("hasRole('enduser')")
    public void clearUserItems() {
      shoppingCartItemRepository.deleteByUserId(authInfo.userId());
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
                item.getTicketType(),
                item.getPriceCents()
        );
    }

    private String extractShowtimeIdFromSeatId(@NotNull String seatId) {
        return seatId.substring(0, 13);
    }


}
