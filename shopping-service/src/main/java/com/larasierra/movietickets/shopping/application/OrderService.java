package com.larasierra.movietickets.shopping.application;

import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.IdUtil;
import com.larasierra.movietickets.shopping.domain.Order;
import com.larasierra.movietickets.shopping.domain.OrderItem;
import com.larasierra.movietickets.shopping.external.apiclient.SeatApiClient;
import com.larasierra.movietickets.shopping.external.jpa.OrderRepository;
import com.larasierra.movietickets.shopping.model.cart.ShoppingCartItemResponse;
import com.larasierra.movietickets.shopping.model.order.CreateOrderRequest;
import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForOrderRequest;
import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForOrderResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final SeatApiClient seatApiClient;
    private final AuthInfo authInfo;

    public OrderService(OrderRepository orderRepository, ShoppingCartService shoppingCartService, SeatApiClient seatApiClient, AuthInfo authInfo) {
        this.orderRepository = orderRepository;
        this.shoppingCartService = shoppingCartService;
        this.seatApiClient = seatApiClient;
        this.authInfo = authInfo;
    }

    @PreAuthorize("hasRole('enduser')")
    public void create(CreateOrderRequest request) {
        // TODO: 26/02/2023 validate purchase token

        // 1. get items from shopping cart
        List<ShoppingCartItemResponse> cartItems = shoppingCartService.findAllUserItems();

        // 2. checks if the seats are available, if so, mark them as no available (this way, even if the token expires, other users can not try to buy them)
        var reserveForOrderRequest = buildReserveSeatForOrderRequest(cartItems, request.purchaseToken());
        List<ReserveSeatForOrderResponse> seats = seatApiClient.reserveForOrder(reserveForOrderRequest);

        // 3. create the order
        var order = buildOrder(cartItems, request.purchaseToken());
        order = orderRepository.save(order);

        // 4. try to make the charged

        // 5. if the charged is successful, update the order paid = true
        orderRepository.markPaid(order.getOrderId());

        // 6. if the charged is not successful, allow the user to try again
    }

    private ReserveSeatForOrderRequest buildReserveSeatForOrderRequest(List<ShoppingCartItemResponse> cartItems, String purchaseToken) {
        List<String> seatIds = cartItems.stream()
                .map(ShoppingCartItemResponse::seatId)
                .toList();

        return new ReserveSeatForOrderRequest(seatIds, purchaseToken);
    }

    private Order buildOrder(List<ShoppingCartItemResponse> cartItems, String purchaseToken) {
        final var order = new Order(
                IdUtil.next(),
                authInfo.userId(),
                purchaseToken,
                false,
                false,
                null,
                OffsetDateTime.now()
        );

        List<OrderItem> items = cartItems.stream()
                .map(cartItem -> new OrderItem(
                        IdUtil.next(),
                        order.getOrderId(),
                        authInfo.userId(),
                        cartItem.seatId(),
                        cartItem.ticketType(),
                        null,
                        OffsetDateTime.now()
                ))
                .toList();

        order.addItems(items);
        return order;
    }
}
