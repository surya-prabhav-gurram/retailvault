package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data @AllArgsConstructor @NoArgsConstructor
public class MonthlySalesDto {
    private String month;
    private Integer monthNum;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
}
