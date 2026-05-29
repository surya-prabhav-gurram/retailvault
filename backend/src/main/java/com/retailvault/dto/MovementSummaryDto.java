package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @AllArgsConstructor @NoArgsConstructor
public class MovementSummaryDto {
    private String movementType;
    private Long totalQuantity;
    private Long eventCount;
}
