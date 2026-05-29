package com.retailvault.repository.oltp;

import com.retailvault.entity.oltp.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("""
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.order o
        JOIN FETCH oi.product p
        JOIN FETCH p.category
        JOIN FETCH p.supplier
        JOIN FETCH o.store
        LEFT JOIN FETCH o.customer
        WHERE o.orderDate >= :since
    """)
    List<OrderItem> findAllWithDetailsSince(LocalDateTime since);
}
