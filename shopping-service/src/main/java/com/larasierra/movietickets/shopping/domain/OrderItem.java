package com.larasierra.movietickets.shopping.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@ToString(exclude = "order")
@Getter @Setter
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseEntity<String> {

    @ValidId
    @Id
    private String orderItemId;

    @ValidId
    @NotNull
    private String orderId;

    @ValidId
    @NotNull
    private String userId;

    @ValidId
    @NotNull
    private String seatId;

    @NotNull
    private String ticketType;

    @Min(1)
    @NotNull
    private Integer priceCents;

    @NotNull
    private OffsetDateTime createdAt;

    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Override
    public String getId() {
        return orderItemId;
    }

    public OrderItem() {}

    public OrderItem(String orderItemId, String orderId, String userId, String seatId, String ticketType, Integer priceCents, OffsetDateTime createdAt) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.userId = userId;
        this.seatId = seatId;
        this.ticketType = ticketType;
        this.priceCents = priceCents;
        this.createdAt = createdAt;
    }

    public void setOrder(@NotNull Order order) {
        this.order = order;
        this.orderId = order.getOrderId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(orderItemId, orderItem.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderItemId);
    }
}
