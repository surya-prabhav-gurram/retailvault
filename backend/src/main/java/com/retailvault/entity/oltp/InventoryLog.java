package com.retailvault.entity.oltp;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory_log", catalog = "retailvault_oltp")
public class InventoryLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(name = "movement_type")
    private String movementType;
    private Integer quantity;
    @Column(name = "stock_before")
    private Integer stockBefore;
    @Column(name = "stock_after")
    private Integer stockAfter;
    @Column(name = "movement_date")
    private LocalDateTime movementDate;
    private String notes;
}
