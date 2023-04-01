package com.larasierra.movietickets.shopping.application;

import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.PurchaseTokenUtil;
import com.larasierra.movietickets.shared.util.TransactionTemplateFactory;
import com.larasierra.movietickets.shopping.external.apiclient.SeatApiClient;
import com.larasierra.movietickets.shopping.external.jpa.OrderRepository;
import com.larasierra.movietickets.shopping.external.stripe.StripeClient;
import com.larasierra.movietickets.shopping.model.cart.ShoppingCartItemResponse;
import com.larasierra.movietickets.shopping.model.order.CreateOrderRequest;
import com.larasierra.movietickets.shopping.model.order.InitOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.AdditionalAnswers.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ShoppingCartService shoppingCartService;
    @Mock
    private SeatApiClient seatApiClient;
    @Mock
    private AuthInfo authInfo;
    @Mock
    private StripeClient stripeClient;
    @Mock
    private PurchaseTokenUtil purchaseTokenUtil;
    @Mock
    private TransactionTemplateFactory transactionTemplateFactory;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        given(transactionTemplateFactory.create()).willReturn(new MockTransactionTemplate());
        orderService = new OrderService(orderRepository, shoppingCartService, seatApiClient, authInfo, stripeClient, purchaseTokenUtil, transactionTemplateFactory);
    }

    @Test
    void create() {
        // given
        given(shoppingCartService.findAllUserItems()).willReturn(shoppingCartItemResponseList());
        given(orderRepository.save(any())).willAnswer(returnsFirstArg());
        given(stripeClient.createPaymentIntent(anyString(), anyLong())).willReturn(new StripeClient.PaymentIntentInfo("pi-id", "clientSecret"));

        var purchaseToken = "pt-test";
        var createOrderRequest = new CreateOrderRequest(purchaseToken);

        // when
        InitOrderResponse response = orderService.create(createOrderRequest);

        // then
        assertNotNull(response.orderId());
        assertEquals(purchaseToken, response.purchaseToken());
        assertEquals("clientSecret", response.clientSecret());

        ArgumentCaptor<Long> totalCentsCaptor = ArgumentCaptor.forClass(Long.class);
        then(stripeClient).should().createPaymentIntent(any(), totalCentsCaptor.capture());
        assertEquals(1600, totalCentsCaptor.getValue());
    }

    @Test
    void create_throwsWhenShoppingCartIsEmpty() {
        // given
        given(shoppingCartService.findAllUserItems()).willReturn(List.of());

        var createOrderRequest = new CreateOrderRequest("");

        // when
        Executable exec = () -> orderService.create(createOrderRequest);

        // then
        AppBadRequestException exception = assertThrows(AppBadRequestException.class, exec);
        assertTrue(exception.getPublicMessage().contains("the cart is empty"));
    }

    private List<ShoppingCartItemResponse> shoppingCartItemResponseList() {
        return List.of(
            new ShoppingCartItemResponse("userId", "0111222333444-1", "s", 800),
            new ShoppingCartItemResponse("userId", "0111222333444-2", "s", 800)
        );
    }

    private static class MockTransactionTemplate extends TransactionTemplate {
        @Override
        public <T> T execute(TransactionCallback<T> action) throws TransactionException {
            return action.doInTransaction(new SimpleTransactionStatus());
        }
    }
}