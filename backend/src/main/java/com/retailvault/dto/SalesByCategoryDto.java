package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data @AllArgsConstructor @NoArgsConstructor
public class SalesByCategoryDto {
    private String category;
    private BigDecimal totalRevenue;
    private Long totalQuantity;
}
