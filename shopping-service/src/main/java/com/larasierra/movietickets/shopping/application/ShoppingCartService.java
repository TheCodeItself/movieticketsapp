package com.larasierra.movietickets.shopping.application;

import com.larasierra.movietickets.shared.exception.AppInternalErrorException;
import com.larasierra.movietickets.shared.exception.AppResourceLockedException;
import com.larasierra.movietickets.shared.exception.UnauthorizedAccessException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.IdUtil;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Service
public class ShoppingCartService {
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final AuthInfo authInfo;
    private final SeatApiClient seatApiClient;
    private final ShowtimeApiClient showtimeApiClient;

    public ShoppingCartService(ShoppingCartItemRepository shoppingCartItemRepository, AuthInfo authInfo, SeatApiClient seatApiClient, ShowtimeApiClient showtimeApiClient) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.authInfo = authInfo;
        this.seatApiClient = seatApiClient;
        this.showtimeApiClient = showtimeApiClient;
    }

    @PreAuthorize("hasRole('enduser')")
    public String generatePurchaseToken() {
        // TODO: 27/02/2023 retrieve the secret
        String secret = "12345";

        String timeSortedId = IdUtil.next();

        // only the user who requested the token can use it to add a seat to the cart or place an order
        String hash = generatePurchaseTokenHash(timeSortedId, authInfo.userId(), secret);

        // delete existing cart items
        shoppingCartItemRepository.deleteByUserId(authInfo.userId());

        return timeSortedId + ":" + hash;
    }

    @PreAuthorize("hasRole('enduser')")
    public void addSeatToCart(AddSeatToCartRequest request) {
        // TODO: 01/03/2023 token expiration need to be validated too
        validateUserPurchaseToken(request.purchaseToken());

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
    public List<ShoppingCartItemResponse> findAllUserItems() {
        return shoppingCartItemRepository.findAllByShoppingCartItemPk_UserId(authInfo.userId())
                .stream()
                .map(this::toDefaultResponse)
                .toList();
    }

    private void validateUserPurchaseToken(String purchaseToken) {
        String secret = "12345";

        String[] split = purchaseToken.split(":");
        String providedTimeSortedId = split[0];
        String providedHash = split[1];

        // only the user who requested the token can use it to add a seat to the cart or place an order
        String calculatedHash = generatePurchaseTokenHash(providedTimeSortedId, authInfo.userId(), secret);

        if (!calculatedHash.equals(providedHash)) {
            throw new UnauthorizedAccessException();
        }
    }

    private String generatePurchaseTokenHash(String timeSortedId, String userId, String secret) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AppInternalErrorException();
        }

        String plainText = timeSortedId + ":" + userId + ":" + secret;

        byte[] hashBytes = messageDigest.digest(plainText.getBytes());

        return Base64.getEncoder().encodeToString(hashBytes);
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
