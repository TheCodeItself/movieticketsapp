package com.larasierra.movietickets.shopping.external.jpa;

import com.larasierra.movietickets.shopping.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = "update Order o set o.paid = true where o.orderId = ?1")
    int markPaid(String orderId);
}
