package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fact_inventory", catalog = "retailvault_warehouse")
public class FactInventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_key")
    private Long inventoryKey;
    @Column(name = "date_key")
    private Integer dateKey;
    @Column(name = "store_key")
    private Integer storeKey;
    @Column(name = "product_key")
    private Integer productKey;
    @Column(name = "supplier_key")
    private Integer supplierKey;
    @Column(name = "movement_type")
    private String movementType;
    @Column(name = "quantity_moved")
    private Integer quantityMoved;
    @Column(name = "stock_before")
    private Integer stockBefore;
    @Column(name = "stock_after")
    private Integer stockAfter;
    @Column(name = "reorder_level")
    private Integer reorderLevel;
    @Column(name = "is_below_reorder")
    private Boolean isBelowReorder;
}
