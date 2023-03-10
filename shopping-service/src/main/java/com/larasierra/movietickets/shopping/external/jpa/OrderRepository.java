package com.larasierra.movietickets.shopping.external.jpa;

import com.larasierra.movietickets.shopping.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = """
        select o
        from Order o join fetch o.items
        where o.userId = ?1 and o.orderId = ?2""")
    Optional<Order> findUserOrderById(String userId, String order);

    @Transactional
    @Modifying
    @Query(value = """
        update Order o
        set o.status = com.larasierra.movietickets.shopping.domain.OrderStatus.SUCCEEDED,
            o.paymentIntentId = ?2,
            o.orderCode = ?3
        where o.orderId = ?1""")
    void confirm(String orderId, String paymentIntentId, UUID orderCode);

    @Transactional
    @Modifying
    @Query(value = """
        update Order o
        set o.status = com.larasierra.movietickets.shopping.domain.OrderStatus.CANCEL,
            o.orderCode = null
        where o.orderId = ?1 and o.paymentIntentId = ?2""")
    void cancel(String orderId, String paymentIntentId);
}
