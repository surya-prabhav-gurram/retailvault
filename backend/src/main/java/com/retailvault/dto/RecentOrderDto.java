package com.retailvault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class RecentOrderDto {
    private Integer orderId;
    private String storeName;
    private String customerName;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String status;
}
