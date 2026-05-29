package com.retailvault.entity.oltp;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customers", catalog = "retailvault_oltp")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String city;
    private String state;
}
