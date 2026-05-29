package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data @AllArgsConstructor @NoArgsConstructor
public class KpiSummaryDto {
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Long totalUnits;
    private Long totalOrders;
    private BigDecimal profitMargin;
}
