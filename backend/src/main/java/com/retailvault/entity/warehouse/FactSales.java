package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "fact_sales", catalog = "retailvault_warehouse")
public class FactSales {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_key")
    private Long salesKey;
    @Column(name = "date_key")
    private Integer dateKey;
    @Column(name = "store_key")
    private Integer storeKey;
    @Column(name = "product_key")
    private Integer productKey;
    @Column(name = "customer_key")
    private Integer customerKey;
    @Column(name = "order_id")
    private Integer orderId;
    private Integer quantity;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "discount_pct")
    private BigDecimal discountPct;
    @Column(name = "gross_revenue")
    private BigDecimal grossRevenue;
    @Column(name = "discount_amount")
    private BigDecimal discountAmount;
    @Column(name = "net_revenue")
    private BigDecimal netRevenue;
    @Column(name = "cost_of_goods")
    private BigDecimal costOfGoods;
    @Column(name = "gross_profit")
    private BigDecimal grossProfit;
}
