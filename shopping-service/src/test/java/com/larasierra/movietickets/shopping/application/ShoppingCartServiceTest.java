package com.larasierra.movietickets.shopping.application;

import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppResourceLockedException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.PurchaseTokenUtil;
import com.larasierra.movietickets.shopping.domain.ShoppingCartItem;
import com.larasierra.movietickets.shopping.external.apiclient.SeatApiClient;
import com.larasierra.movietickets.shopping.external.apiclient.ShowtimeApiClient;
import com.larasierra.movietickets.shopping.external.jpa.ShoppingCartItemRepository;
import com.larasierra.movietickets.shopping.model.cart.AddSeatToCartRequest;
import com.larasierra.movietickets.shopping.model.showtime.ShowtimeApiResponse;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartItemRepository shoppingCartItemRepository;
    @Mock
    private AuthInfo authInfo;
    @Mock
    private SeatApiClient seatApiClient;
    @Mock
    private ShowtimeApiClient showtimeApiClient;
    @Mock
    private PurchaseTokenUtil purchaseTokenUtil;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    @Test
    void generatePurchaseToken() {
        // given
        var userId = "0111222333444";
        var expectedToken = "test-token";
        given(authInfo.userId()).willReturn(userId);
        given(purchaseTokenUtil.generateToken(any(), eq(userId))).willReturn(expectedToken);

        // when
        String token = shoppingCartService.generatePurchaseToken();

        // then
        then(shoppingCartItemRepository).should().deleteByUserId(userId);
        assertEquals(expectedToken, token);
    }

    @ParameterizedTest
    @CsvSource({"s,800", "e,400"})
    void addSeatToCart(String ticketType, Integer expectedPrice) {
        // given
        var request = new AddSeatToCartRequest("0111222333444-1", ticketType, "seatToken", "purchaseToken");
        given(showtimeApiClient.findById(any())).willReturn(showtimeApiResponse());

        // when
        shoppingCartService.addSeatToCart(request);

        // then
        ArgumentCaptor<ShoppingCartItem> itemCaptor = ArgumentCaptor.forClass(ShoppingCartItem.class);
        then(shoppingCartItemRepository).should().save(itemCaptor.capture());

        var item = itemCaptor.getValue();
        assertNotNull(item.getShoppingCartItemPk());
        assertEquals(ticketType, item.getTicketType());
        assertEquals(expectedPrice, item.getPriceCents());
    }

    @Test
    void addSeatToCart_throwsWhenPurchaseTokenHasExpired() {
        // given
        var request = new AddSeatToCartRequest("0111222333444-1", "s", "seatToken", "purchaseToken");
        given(purchaseTokenUtil.isTokenExpired(request.purchaseToken())).willReturn(true);

        // when
        Executable exec = () -> shoppingCartService.addSeatToCart(request);

        // then
        AppBadRequestException exception = assertThrows(AppBadRequestException.class, exec);
        assertTrue(exception.getPublicMessage().contains("the token has expired"));
    }

    @Test
    void addSeatToCart_throwsWhenConflictOnReservingSeats() {
        // given
        var request = new AddSeatToCartRequest("0111222333444-1", "s", "seatToken", "purchaseToken");
        willThrow(FeignException.Conflict.class).given(seatApiClient).reserveForCart(any(), any());

        // when
        Executable exec = () -> shoppingCartService.addSeatToCart(request);

        // then
        assertThrows(AppResourceLockedException.class, exec);
    }

    private ShowtimeApiResponse showtimeApiResponse() {
        return new ShowtimeApiResponse(
                "0111222333444",
                "0222333444555",
                "0333444555666",
                "0444555666777",
                OffsetDateTime.now().plusDays(1),
                800,
                400,
                OffsetDateTime.now()
        );
    }
}