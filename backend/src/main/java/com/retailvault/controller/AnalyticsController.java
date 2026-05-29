package com.retailvault.controller;

import com.retailvault.dto.*;
import com.retailvault.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/kpi")
    public ResponseEntity<ApiResponse<KpiSummaryDto>> getKpi(
            @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getKpiSummary(y)));
    }

    @GetMapping("/sales/by-store")
    public ResponseEntity<ApiResponse<List<SalesByStoreDto>>> getSalesByStore(
            @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getSalesByStore(y)));
    }

    @GetMapping("/sales/by-category")
    public ResponseEntity<ApiResponse<List<SalesByCategoryDto>>> getSalesByCategory(
            @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getSalesByCategory(y)));
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<ApiResponse<List<MonthlySalesDto>>> getMonthlySales(
            @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getMonthlySales(y)));
    }

    @GetMapping("/sales/top-products")
    public ResponseEntity<ApiResponse<List<TopProductDto>>> getTopProducts(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "10") int topN) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getTopProducts(y, topN)));
    }

    @GetMapping("/sales/by-region")
    public ResponseEntity<ApiResponse<List<RegionSalesDto>>> getSalesByRegion(
            @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getSalesByRegion(y)));
    }

    @GetMapping("/inventory/turnover")
    public ResponseEntity<ApiResponse<List<InventoryTurnoverDto>>> getInventoryTurnover() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getInventoryTurnover()));
    }

    @GetMapping("/inventory/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockAlertDto>>> getLowStockAlerts() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getLowStockAlerts()));
    }

    @GetMapping("/inventory/movements")
    public ResponseEntity<ApiResponse<List<MovementSummaryDto>>> getInventoryMovements(
            @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getInventoryMovementSummary(y)));
    }
}
