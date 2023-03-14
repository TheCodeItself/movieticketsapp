package com.larasierra.movietickets.shopping.application;

import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.IdUtil;
import com.larasierra.movietickets.shared.util.PurchaseTokenUtil;
import com.larasierra.movietickets.shopping.domain.Order;
import com.larasierra.movietickets.shopping.domain.OrderItem;
import com.larasierra.movietickets.shopping.domain.OrderStatus;
import com.larasierra.movietickets.shopping.external.stripe.StripeClient;
import com.larasierra.movietickets.shopping.external.apiclient.SeatApiClient;
import com.larasierra.movietickets.shopping.external.jpa.OrderRepository;
import com.larasierra.movietickets.shopping.model.cart.ShoppingCartItemResponse;
import com.larasierra.movietickets.shopping.model.order.CreateOrderRequest;
import com.larasierra.movietickets.shopping.model.order.DefaultOrderItemResponse;
import com.larasierra.movietickets.shopping.model.order.DefaultOrderResponse;
import com.larasierra.movietickets.shopping.model.order.InitOrderResponse;
import com.larasierra.movietickets.shopping.model.seat.ReserveSeatForOrderRequest;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Value("${com.larasierra.stripe_whsec}")
    private String STRIPE_WHSEC;

    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final SeatApiClient seatApiClient;
    private final AuthInfo authInfo;
    private final StripeClient stripeClient;
    private final PurchaseTokenUtil purchaseTokenUtil;

    public OrderService(OrderRepository orderRepository, ShoppingCartService shoppingCartService, SeatApiClient seatApiClient, AuthInfo authInfo, StripeClient stripeClient, PurchaseTokenUtil purchaseTokenUtil) {
        this.orderRepository = orderRepository;
        this.shoppingCartService = shoppingCartService;
        this.seatApiClient = seatApiClient;
        this.authInfo = authInfo;
        this.stripeClient = stripeClient;
        this.purchaseTokenUtil = purchaseTokenUtil;
    }

    @PreAuthorize("hasRole('enduser')")
    public InitOrderResponse create(CreateOrderRequest request) {
        purchaseTokenUtil.validateToken(authInfo.userId(), request.purchaseToken());

        // 1. get items from shopping cart
        List<ShoppingCartItemResponse> cartItems = shoppingCartService.findAllUserItems();
        if (cartItems.isEmpty()) {
            throw new AppBadRequestException("the cart is empty");
        }

        // 2. checks if the seats are available, if so, mark them as no available (this way, even if the token expires, other users can not try to buy them)
        var reserveForOrderRequest = buildReserveSeatForOrderRequest(cartItems, request.purchaseToken());
        seatApiClient.reserveForOrder(reserveForOrderRequest);

        // 3. create the order
        var order = buildOrder(cartItems, request.purchaseToken());
        order = orderRepository.save(order);

        // 4. create payment intent
        StripeClient.PaymentIntentInfo intent = stripeClient.createPaymentIntent(order.getOrderId(), order.getTotalCents());

        return new InitOrderResponse(order.getOrderId(), intent.clientSecret(), order.getPurchaseToken());
    }

    @PreAuthorize("permitAll()")
    public void processPaymentEvent(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, STRIPE_WHSEC);
        } catch (SignatureVerificationException ex) {
            throw new AppBadRequestException("");
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        StripeObject stripeObject = dataObjectDeserializer.getObject()
                .orElseThrow(() -> new AppBadRequestException(""));

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                String orderId = paymentIntent.getMetadata().get("order_id");
                orderRepository.confirm(orderId, paymentIntent.getId(), UUID.randomUUID());
            }
            case "payment_intent.processing" -> System.out.println("payment_intent.processing");
            case "payment_intent.payment_failed" -> System.out.println("payment_intent.payment_failed");
            case "payment_intent.canceled" -> {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                String orderId = paymentIntent.getMetadata().get("orderId");
                orderRepository.cancel(orderId, paymentIntent.getId());
                // release tickets
            }
            default -> System.out.println("Unhandled event type: " + event.getType());
        }
    }

    /**
     * Find the order with the given id, and fetch its details. It also adds the current user's id to the query as a filter.
     * @param orderId the order's id
     * @return the order and its details
     */
    @PreAuthorize("hasRole('enduser')")
    public Optional<DefaultOrderResponse> findUserOrderById(String orderId) {
        return orderRepository.findUserOrderById(authInfo.userId(), orderId)
                .map(this::toDefaultResponse);
    }

    private ReserveSeatForOrderRequest buildReserveSeatForOrderRequest(List<ShoppingCartItemResponse> cartItems, String purchaseToken) {
        List<String> seatIds = cartItems.stream()
                .map(ShoppingCartItemResponse::seatId)
                .toList();

        return new ReserveSeatForOrderRequest(seatIds, purchaseToken);
    }

    private Order buildOrder(List<ShoppingCartItemResponse> cartItems, String purchaseToken) {
        long totalCents = cartItems.stream()
                .mapToLong(ShoppingCartItemResponse::priceCents)
                .sum();

        final var order = Order.builder()
                .orderId(IdUtil.next())
                .userId(authInfo.userId())
                .purchaseToken(purchaseToken)
                .status(OrderStatus.PENDING)
                .cancel(false)
                .totalCents(totalCents)
                .createdAt(OffsetDateTime.now())
                .build();

        List<OrderItem> items = cartItems.stream()
                .map(cartItem ->
                    OrderItem.builder()
                        .orderItemId(IdUtil.next())
                        .orderId(order.getOrderId())
                        .userId(authInfo.userId())
                        .seatId(cartItem.seatId())
                        .ticketType(cartItem.ticketType())
                        .priceCents(cartItem.priceCents())
                        .createdAt(OffsetDateTime.now())
                        .build()
                )
                .toList();

        order.addItems(items);
        return order;
    }

    private DefaultOrderResponse toDefaultResponse(Order order) {
        List<DefaultOrderItemResponse> items = order.getItems().stream()
                .map(item -> new DefaultOrderItemResponse(
                        item.getOrderItemId(),
                        item.getOrderId(),
                        item.getUserId(),
                        item.getSeatId(),
                        item.getTicketType(),
                        item.getPriceCents(),
                        item.getCreatedAt()
                ))
                .toList();

        return new DefaultOrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getPurchaseToken(),
                order.getStatus().getCode(),
                order.getCancel(),
                order.getTotalCents(),
                order.getCreatedAt(),
                items
        );
    }
}
