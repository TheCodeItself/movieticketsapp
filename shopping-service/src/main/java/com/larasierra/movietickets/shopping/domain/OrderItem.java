package com.larasierra.movietickets.shopping.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Builder
@AllArgsConstructor
@ToString(exclude = "order")
@Getter @Setter
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseEntity<String> {

    @ValidId
    @Id
    @Column(name = "order_item_id")
    private String orderItemId;

    @ValidId
    @NotNull
    @Column(name = "order_id")
    private String orderId;

    @ValidId
    @NotNull
    @Column(name = "user_id")
    private String userId;

    @NotNull
    @Column(name = "seat_id")
    private String seatId;

    @NotNull
    @Column(name = "ticket_type")
    private String ticketType;

    @Min(1)
    @NotNull
    @Column(name = "price_cents")
    private Integer priceCents;

    @NotNull
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Override
    public String getId() {
        return orderItemId;
    }

    public OrderItem() {}

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
