package com.retailvault.entity.oltp;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "suppliers", catalog = "retailvault_oltp")
public class Supplier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Integer supplierId;
    @Column(name = "supplier_name")
    private String supplierName;
    @Column(name = "contact_name")
    private String contactName;
    private String email;
    private String phone;
    private String country;
}
