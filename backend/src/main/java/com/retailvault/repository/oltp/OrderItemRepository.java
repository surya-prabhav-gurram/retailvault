package com.retailvault.repository.oltp;

import com.retailvault.entity.oltp.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<OrderItem> findAllWithDetailsSince(@Param("since") LocalDateTime since);

    @Query(value = """
        SELECT o.order_id, s.store_name,
               CONCAT(c.first_name, ' ', c.last_name) as customer_name,
               o.total_amount, o.order_date, o.status
        FROM retailvault_oltp.orders o
        JOIN retailvault_oltp.stores s ON o.store_id = s.store_id
        LEFT JOIN retailvault_oltp.customers c ON o.customer_id = c.customer_id
        ORDER BY o.order_date DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findRecentOrders();
}
