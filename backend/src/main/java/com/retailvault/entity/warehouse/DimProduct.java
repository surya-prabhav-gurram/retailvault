package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "dim_product", catalog = "retailvault_warehouse")
public class DimProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_key")
    private Integer productKey;
    @Column(name = "product_id")
    private Integer productId;
    private String sku;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "category_name")
    private String categoryName;
    @Column(name = "parent_category")
    private String parentCategory;
    @Column(name = "supplier_name")
    private String supplierName;
    @Column(name = "supplier_country")
    private String supplierCountry;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "cost_price")
    private BigDecimal costPrice;
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    @Column(name = "is_current")
    private Boolean isCurrent;
}
