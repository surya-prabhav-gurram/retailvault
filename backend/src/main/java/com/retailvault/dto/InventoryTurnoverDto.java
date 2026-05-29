package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @AllArgsConstructor @NoArgsConstructor
public class InventoryTurnoverDto {
    private String productName;
    private String storeName;
    private Long totalSold;
    private Double avgStock;
    private Integer minStock;
}
