package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @AllArgsConstructor @NoArgsConstructor
public class LowStockAlertDto {
    private String productName;
    private String storeName;
    private Integer currentStock;
    private Integer reorderLevel;
}
