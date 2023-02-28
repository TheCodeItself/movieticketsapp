package com.larasierra.movietickets.shopping.domain;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@ToString(exclude = "items")
@Getter @Setter
@Table(name = "orders")
@Entity
public class Order {
    @ValidId
    @Id
    private String orderId;

    @ValidId
    @NotNull
    private String userId;

    @NotBlank
    @NotNull
    private String purchaseToken;

    @NotNull
    private Boolean paid;

    @NotNull
    private Boolean cancel;

    @Min(1)
    @NotNull
    private Long totalCents;

    @NotNull
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public void addItem(@NotNull OrderItem orderItem) {
        orderItem.setOrder(this);
        items.add(orderItem);
    }

    public void addItems(@NotNull List<OrderItem> items) {
        items.forEach(this::addItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
