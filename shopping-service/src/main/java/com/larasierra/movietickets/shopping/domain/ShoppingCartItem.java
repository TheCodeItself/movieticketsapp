package com.larasierra.movietickets.shopping.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "shopping_cart_item")
@Entity
public class ShoppingCartItem extends BaseEntity<ShoppingCartItemPk> {
    @EmbeddedId
    private ShoppingCartItemPk shoppingCartItemPk;

    @NotNull
    private String ticketType;

    @Min(1)
    @NotNull
    private Integer priceCents;

    @Override
    public ShoppingCartItemPk getId() {
        return shoppingCartItemPk;
    }
}
