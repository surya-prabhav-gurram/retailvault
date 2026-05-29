package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_supplier", catalog = "retailvault_warehouse")
public class DimSupplier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_key")
    private Integer supplierKey;
    @Column(name = "supplier_id")
    private Integer supplierId;
    @Column(name = "supplier_name")
    private String supplierName;
    @Column(name = "contact_name")
    private String contactName;
    private String country;
    @Column(name = "is_current")
    private Boolean isCurrent;
}
