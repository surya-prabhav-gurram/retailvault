package com.retailvault.service;

import com.retailvault.dto.*;
import com.retailvault.entity.warehouse.EtlRunLog;
import com.retailvault.repository.warehouse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final FactSalesRepository factSalesRepository;
    private final FactInventoryRepository factInventoryRepository;
    private final EtlRunLogRepository etlRunLogRepository;

    public KpiSummaryDto getKpiSummary(int year) {
        java.util.List<Object[]> results = factSalesRepository.getKpiSummary(year);
        Object[] row = results.isEmpty() ? new Object[]{0,0,0,0} : results.get(0);
        BigDecimal revenue = toBigDecimal(row[0]);
        BigDecimal profit = toBigDecimal(row[1]);
        Long units = toLong(row[2]);
        Long orders = toLong(row[3]);
        BigDecimal margin = revenue.compareTo(BigDecimal.ZERO) > 0
                ? profit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        return new KpiSummaryDto(revenue, profit, units, orders, margin.setScale(2, RoundingMode.HALF_UP));
    }

    public List<SalesByStoreDto> getSalesByStore(int year) {
        return factSalesRepository.getSalesByStore(year).stream()
                .map(r -> new SalesByStoreDto(
                        (String) r[0],
                        toBigDecimal(r[1]),
                        toLong(r[2]),
                        toBigDecimal(r[3])
                )).collect(Collectors.toList());
    }

    public List<SalesByCategoryDto> getSalesByCategory(int year) {
        return factSalesRepository.getSalesByCategory(year).stream()
                .map(r -> new SalesByCategoryDto(
                        (String) r[0],
                        toBigDecimal(r[1]),
                        toLong(r[2])
                )).collect(Collectors.toList());
    }

    public List<MonthlySalesDto> getMonthlySales(int year) {
        return factSalesRepository.getMonthlySales(year).stream()
                .map(r -> new MonthlySalesDto(
                        (String) r[0],
                        ((Number) r[1]).intValue(),
                        toBigDecimal(r[2]),
                        toBigDecimal(r[3])
                )).collect(Collectors.toList());
    }

    public List<TopProductDto> getTopProducts(int year, int topN) {
        return factSalesRepository.getTopProducts(year, topN).stream()
                .map(r -> new TopProductDto(
                        (String) r[0],
                        (String) r[1],
                        toBigDecimal(r[2]),
                        toLong(r[3]),
                        toBigDecimal(r[4])
                )).collect(Collectors.toList());
    }

    public List<RegionSalesDto> getSalesByRegion(int year) {
        return factSalesRepository.getSalesByRegion(year).stream()
                .map(r -> new RegionSalesDto(
                        (String) r[0],
                        toBigDecimal(r[1]),
                        toLong(r[2])
                )).collect(Collectors.toList());
    }

    public List<InventoryTurnoverDto> getInventoryTurnover() {
        return factInventoryRepository.getInventoryTurnover().stream()
                .map(r -> new InventoryTurnoverDto(
                        (String) r[0],
                        (String) r[1],
                        toLong(r[2]),
                        r[3] != null ? ((Number) r[3]).doubleValue() : 0.0,
                        r[4] != null ? ((Number) r[4]).intValue() : 0
                )).collect(Collectors.toList());
    }

    public List<LowStockAlertDto> getLowStockAlerts() {
        return factInventoryRepository.getLowStockAlerts().stream()
                .map(r -> new LowStockAlertDto(
                        (String) r[0],
                        (String) r[1],
                        r[2] != null ? ((Number) r[2]).intValue() : 0,
                        r[3] != null ? ((Number) r[3]).intValue() : 0
                )).collect(Collectors.toList());
    }

    public List<MovementSummaryDto> getInventoryMovementSummary(int year) {
        return factInventoryRepository.getInventoryMovementSummary(year).stream()
                .map(r -> new MovementSummaryDto(
                        (String) r[0],
                        toLong(r[1]),
                        toLong(r[2])
                )).collect(Collectors.toList());
    }

    public List<EtlRunLogDto> getEtlHistory() {
        return etlRunLogRepository.findTop20ByOrderByStartedAtDesc().stream()
                .map(this::toEtlDto).collect(Collectors.toList());
    }

    private EtlRunLogDto toEtlDto(EtlRunLog log) {
        Long duration = null;
        if (log.getStartedAt() != null && log.getCompletedAt() != null) {
            duration = Duration.between(log.getStartedAt(), log.getCompletedAt()).getSeconds();
        }
        return new EtlRunLogDto(
                log.getRunId(), log.getJobName(), log.getStatus(),
                log.getStartedAt(), log.getCompletedAt(),
                log.getRowsExtracted(), log.getRowsLoaded(),
                log.getErrorMessage(), log.getTriggeredBy(), duration
        );
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof byte[]) {
            try { return new BigDecimal(new String((byte[]) val)); } catch (Exception e) { return BigDecimal.ZERO; }
        }
        if (val instanceof Double) return BigDecimal.valueOf((Double) val);
        if (val instanceof Float) return BigDecimal.valueOf(((Float) val).doubleValue());
        if (val instanceof Long) return BigDecimal.valueOf((Long) val);
        if (val instanceof Integer) return BigDecimal.valueOf(((Integer) val).longValue());
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        try { return new BigDecimal(val.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private Long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof byte[]) {
            try { return new BigDecimal(new String((byte[]) val)).longValue(); } catch (Exception e) { return 0L; }
        }
        if (val instanceof Number) return ((Number) val).longValue();
        try { return new BigDecimal(val.toString()).longValue(); } catch (Exception e) { return 0L; }
    }
}
