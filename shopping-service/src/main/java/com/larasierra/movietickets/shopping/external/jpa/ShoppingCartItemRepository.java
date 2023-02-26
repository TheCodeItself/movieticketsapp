package com.larasierra.movietickets.shopping.external.jpa;

import com.larasierra.movietickets.shopping.domain.ShoppingCartItem;
import com.larasierra.movietickets.shopping.domain.ShoppingCartItemPk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, ShoppingCartItemPk> {
    List<ShoppingCartItem> findAllByShoppingCartItemPk_UserId(String userId);
}
