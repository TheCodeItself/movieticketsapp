package com.larasierra.movietickets.shopping.external.jpa;

import com.larasierra.movietickets.shopping.domain.ShoppingCartItem;
import com.larasierra.movietickets.shopping.domain.ShoppingCartItemPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, ShoppingCartItemPk> {
    List<ShoppingCartItem> findAllByShoppingCartItemPk_UserId(String userId);

    @Modifying
    @Query("delete from ShoppingCartItem item where item.shoppingCartItemPk.userId = ?1")
    @Transactional
    void deleteByUserId(String userId);
}
