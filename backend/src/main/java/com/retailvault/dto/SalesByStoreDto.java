package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data @AllArgsConstructor @NoArgsConstructor
public class SalesByStoreDto {
    private String storeName;
    private BigDecimal totalRevenue;
    private Long totalQuantity;
    private BigDecimal totalProfit;
}
