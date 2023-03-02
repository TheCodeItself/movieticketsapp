package com.larasierra.movietickets.shopping.external.jpa;

import com.larasierra.movietickets.shopping.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Transactional
    @Modifying
    @Query(value = "update Order o set o.paid = true where o.orderId = ?1")
    int markPaid(String orderId);
}
