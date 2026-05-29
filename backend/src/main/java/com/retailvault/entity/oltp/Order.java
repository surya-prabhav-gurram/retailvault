package com.retailvault.entity.oltp;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders", catalog = "retailvault_oltp")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    private String status;
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items;
}
