package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_customer", catalog = "retailvault_warehouse")
public class DimCustomer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_key")
    private Integer customerKey;
    @Column(name = "customer_id")
    private Integer customerId;
    @Column(name = "full_name")
    private String fullName;
    private String city;
    private String state;
    @Column(name = "is_current")
    private Boolean isCurrent;
}
