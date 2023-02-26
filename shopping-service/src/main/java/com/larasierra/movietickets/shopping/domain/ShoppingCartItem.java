package com.larasierra.movietickets.shopping.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    @Override
    public ShoppingCartItemPk getId() {
        return shoppingCartItemPk;
    }
}
