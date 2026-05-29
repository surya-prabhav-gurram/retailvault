package com.retailvault.entity.oltp;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items", catalog = "retailvault_oltp")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    private BigDecimal discount;
    @Column(name = "line_total", insertable = false, updatable = false)
    private BigDecimal lineTotal;
}
