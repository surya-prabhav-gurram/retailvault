package com.retailvault.entity.oltp;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stores", catalog = "retailvault_oltp")
public class Store {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
