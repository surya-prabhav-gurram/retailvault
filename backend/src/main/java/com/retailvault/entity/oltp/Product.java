package com.retailvault.entity.oltp;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products", catalog = "retailvault_oltp")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    private String sku;
    @Column(name = "product_name")
    private String productName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "cost_price")
    private BigDecimal costPrice;
}
