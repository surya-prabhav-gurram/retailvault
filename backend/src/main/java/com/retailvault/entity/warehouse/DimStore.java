package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "dim_store", catalog = "retailvault_warehouse")
public class DimStore {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_key")
    private Integer storeKey;
    @Column(name = "store_id")
    private Integer storeId;
    @Column(name = "store_name")
    private String storeName;
    private String city;
    private String state;
    private String region;
    @Column(name = "store_type")
    private String storeType;
    @Column(name = "opened_date")
    private LocalDate openedDate;
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    @Column(name = "is_current")
    private Boolean isCurrent;
}
