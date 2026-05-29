package com.retailvault.etl;

import com.retailvault.entity.oltp.*;
import com.retailvault.entity.warehouse.*;
import com.retailvault.repository.oltp.*;
import com.retailvault.repository.warehouse.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtlPipelineService {

    // OLTP source repositories
    private final OrderItemRepository orderItemRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final StoreOltpRepository storeOltpRepository;
    private final ProductOltpRepository productOltpRepository;
    private final CustomerOltpRepository customerOltpRepository;
    private final SupplierOltpRepository supplierOltpRepository;

    // Warehouse dimension repositories
    private final DimDateRepository dimDateRepository;
    private final DimStoreRepository dimStoreRepository;
    private final DimProductRepository dimProductRepository;
    private final DimSupplierRepository dimSupplierRepository;
    private final DimCustomerRepository dimCustomerRepository;

    // Warehouse fact & audit repositories
    private final FactSalesRepository factSalesRepository;
    private final FactInventoryRepository factInventoryRepository;
    private final EtlRunLogRepository etlRunLogRepository;

    // ============================================================
    // Orchestration
    // ============================================================
    public EtlRunLog runFullEtl(String triggeredBy) {
        EtlRunLog runLog = new EtlRunLog();
        runLog.setJobName("FULL_ETL");
        runLog.setStatus("RUNNING");
        runLog.setStartedAt(LocalDateTime.now());
        runLog.setTriggeredBy(triggeredBy);
        runLog.setRowsExtracted(0);
        runLog.setRowsLoaded(0);
        etlRunLogRepository.save(runLog);

        try {
            log.info("Starting ETL pipeline - triggered by: {}", triggeredBy);

            // Step 1: Populate dim_date (last 2 years + next 1 year)
            int dateRows = populateDimDate();
            log.info("Dim Date: {} rows", dateRows);

            // Step 2: Load dimensions from OLTP
            int dimRows = loadDimensions();
            log.info("Dimensions loaded: {} rows", dimRows);

            // Step 3: Load fact_sales
            LocalDateTime since = LocalDateTime.now().minusYears(2);
            List<OrderItem> orderItems = orderItemRepository.findAllWithDetailsSince(since);
            int salesRows = loadFactSales(orderItems);
            log.info("Fact Sales: {} rows", salesRows);

            // Step 4: Load fact_inventory
            List<InventoryLog> inventoryLogs = inventoryLogRepository.findAllWithDetailsSince(since);
            int invRows = loadFactInventory(inventoryLogs);
            log.info("Fact Inventory: {} rows", invRows);

            int total = dateRows + dimRows + salesRows + invRows;
            runLog.setStatus("SUCCESS");
            runLog.setRowsExtracted(orderItems.size() + inventoryLogs.size());
            runLog.setRowsLoaded(total);
            runLog.setCompletedAt(LocalDateTime.now());
            log.info("ETL pipeline completed. Total rows loaded: {}", total);

        } catch (Exception e) {
            log.error("ETL pipeline failed: {}", e.getMessage(), e);
            runLog.setStatus("FAILED");
            runLog.setErrorMessage(e.getMessage());
            runLog.setCompletedAt(LocalDateTime.now());
        }

        return etlRunLogRepository.save(runLog);
    }

    // ============================================================
    // Dim Date Population
    // ============================================================
    @Transactional("warehouseTransactionManager")
    public int populateDimDate() {
        LocalDate start = LocalDate.now().minusYears(2);
        LocalDate end = LocalDate.now().plusYears(1);
        int count = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int dateKey = Integer.parseInt(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            if (dimDateRepository.findByDateKey(dateKey).isEmpty()) {
                DimDate d = new DimDate();
                d.setDateKey(dateKey);
                d.setFullDate(date);
                d.setDayOfWeek(date.getDayOfWeek().getValue());
                d.setDayName(date.getDayOfWeek().toString());
                d.setDayOfMonth(date.getDayOfMonth());
                d.setDayOfYear(date.getDayOfYear());
                d.setWeekOfYear(date.get(java.time.temporal.WeekFields.ISO.weekOfYear()));
                d.setMonthNum(date.getMonthValue());
                d.setMonthName(date.getMonth().toString());
                d.setQuarter((date.getMonthValue() - 1) / 3 + 1);
                d.setYear(date.getYear());
                d.setIsWeekend(date.getDayOfWeek().getValue() >= 6);
                d.setIsHoliday(false);
                dimDateRepository.save(d);
                count++;
            }
        }
        return count;
    }

    // ============================================================
    // Load Dimensions (SCD Type 2)
    // ============================================================
    @Transactional("warehouseTransactionManager")
    public int loadDimensions() {
        int count = 0;

        // Load stores
        for (Store s : storeOltpRepository.findAll()) {
            if (dimStoreRepository.findByStoreIdAndIsCurrent(s.getStoreId(), true).isEmpty()) {
                DimStore ds = new DimStore();
                ds.setStoreId(s.getStoreId());
                ds.setStoreName(s.getStoreName());
                ds.setCity(s.getCity());
                ds.setState(s.getState());
                ds.setRegion(s.getRegion());
                ds.setStoreType(s.getStoreType());
                ds.setOpenedDate(s.getOpenedDate());
                ds.setEffectiveDate(LocalDate.now());
                ds.setIsCurrent(true);
                dimStoreRepository.save(ds);
                count++;
            }
        }

        // Load products
        for (Product p : productOltpRepository.findAll()) {
            if (dimProductRepository.findByProductIdAndIsCurrent(p.getProductId(), true).isEmpty()) {
                DimProduct dp = new DimProduct();
                dp.setProductId(p.getProductId());
                dp.setSku(p.getSku());
                dp.setProductName(p.getProductName());
                dp.setCategoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : "Unknown");
                dp.setParentCategory(p.getCategory() != null ? p.getCategory().getParentCategory() : "Unknown");
                dp.setSupplierName(p.getSupplier() != null ? p.getSupplier().getSupplierName() : "Unknown");
                dp.setSupplierCountry(p.getSupplier() != null ? p.getSupplier().getCountry() : "Unknown");
                dp.setUnitPrice(p.getUnitPrice());
                dp.setCostPrice(p.getCostPrice());
                dp.setEffectiveDate(LocalDate.now());
                dp.setIsCurrent(true);
                dimProductRepository.save(dp);
                count++;
            }
        }

        // Load suppliers
        for (Supplier s : supplierOltpRepository.findAll()) {
            if (dimSupplierRepository.findBySupplierIdAndIsCurrent(s.getSupplierId(), true).isEmpty()) {
                DimSupplier ds = new DimSupplier();
                ds.setSupplierId(s.getSupplierId());
                ds.setSupplierName(s.getSupplierName());
                ds.setContactName(s.getContactName());
                ds.setCountry(s.getCountry());
                ds.setIsCurrent(true);
                dimSupplierRepository.save(ds);
                count++;
            }
        }

        // Load customers
        for (Customer c : customerOltpRepository.findAll()) {
            if (dimCustomerRepository.findByCustomerIdAndIsCurrent(c.getCustomerId(), true).isEmpty()) {
                DimCustomer dc = new DimCustomer();
                dc.setCustomerId(c.getCustomerId());
                dc.setFullName(c.getFirstName() + " " + c.getLastName());
                dc.setCity(c.getCity());
                dc.setState(c.getState());
                dc.setIsCurrent(true);
                dimCustomerRepository.save(dc);
                count++;
            }
        }

        return count;
    }

    // ============================================================
    // Load Fact Sales
    // ============================================================
    @Transactional("warehouseTransactionManager")
    public int loadFactSales(List<OrderItem> orderItems) {
        int count = 0;
        for (OrderItem oi : orderItems) {
            try {
                LocalDate orderDate = oi.getOrder().getOrderDate().toLocalDate();
                int dateKey = Integer.parseInt(orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

                Integer storeKey = dimStoreRepository
                        .findByStoreIdAndIsCurrent(oi.getOrder().getStore().getStoreId(), true)
                        .map(DimStore::getStoreKey).orElse(null);
                Integer productKey = dimProductRepository
                        .findByProductIdAndIsCurrent(oi.getProduct().getProductId(), true)
                        .map(DimProduct::getProductKey).orElse(null);

                if (storeKey == null || productKey == null) continue;

                Integer customerKey = null;
                if (oi.getOrder().getCustomer() != null) {
                    customerKey = dimCustomerRepository
                            .findByCustomerIdAndIsCurrent(oi.getOrder().getCustomer().getCustomerId(), true)
                            .map(DimCustomer::getCustomerKey).orElse(null);
                }

                BigDecimal qty = BigDecimal.valueOf(oi.getQuantity());
                BigDecimal price = oi.getUnitPrice();
                BigDecimal discountPct = oi.getDiscount() != null ? oi.getDiscount() : BigDecimal.ZERO;
                BigDecimal grossRevenue = price.multiply(qty);
                BigDecimal discountAmount = grossRevenue.multiply(discountPct)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal netRevenue = grossRevenue.subtract(discountAmount);
                BigDecimal costOfGoods = oi.getProduct().getCostPrice() != null
                        ? oi.getProduct().getCostPrice().multiply(qty) : BigDecimal.ZERO;
                BigDecimal grossProfit = netRevenue.subtract(costOfGoods);

                FactSales fs = new FactSales();
                fs.setDateKey(dateKey);
                fs.setStoreKey(storeKey);
                fs.setProductKey(productKey);
                fs.setCustomerKey(customerKey);
                fs.setOrderId(oi.getOrder().getOrderId());
                fs.setQuantity(oi.getQuantity());
                fs.setUnitPrice(price);
                fs.setDiscountPct(discountPct);
                fs.setGrossRevenue(grossRevenue);
                fs.setDiscountAmount(discountAmount);
                fs.setNetRevenue(netRevenue);
                fs.setCostOfGoods(costOfGoods);
                fs.setGrossProfit(grossProfit);

                factSalesRepository.save(fs);
                count++;
            } catch (Exception e) {
                log.warn("Skipping order item {}: {}", oi.getItemId(), e.getMessage());
            }
        }
        return count;
    }

    // ============================================================
    // Load Fact Inventory
    // ============================================================
    @Transactional("warehouseTransactionManager")
    public int loadFactInventory(List<InventoryLog> logs) {
        int count = 0;
        for (InventoryLog il : logs) {
            try {
                LocalDate movDate = il.getMovementDate().toLocalDate();
                int dateKey = Integer.parseInt(movDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

                Integer storeKey = dimStoreRepository
                        .findByStoreIdAndIsCurrent(il.getStore().getStoreId(), true)
                        .map(DimStore::getStoreKey).orElse(null);
                Integer productKey = dimProductRepository
                        .findByProductIdAndIsCurrent(il.getProduct().getProductId(), true)
                        .map(DimProduct::getProductKey).orElse(null);

                if (storeKey == null || productKey == null) continue;

                Integer supplierKey = (il.getProduct().getSupplier() != null)
                        ? dimSupplierRepository
                              .findBySupplierIdAndIsCurrent(il.getProduct().getSupplier().getSupplierId(), true)
                              .map(DimSupplier::getSupplierKey).orElse(null)
                        : null;

                int reorderLevel = 15;
                boolean isBelowReorder = il.getStockAfter() != null && il.getStockAfter() < reorderLevel;

                FactInventory fi = new FactInventory();
                fi.setDateKey(dateKey);
                fi.setStoreKey(storeKey);
                fi.setProductKey(productKey);
                fi.setSupplierKey(supplierKey);
                fi.setMovementType(il.getMovementType());
                fi.setQuantityMoved(il.getQuantity());
                fi.setStockBefore(il.getStockBefore());
                fi.setStockAfter(il.getStockAfter());
                fi.setReorderLevel(reorderLevel);
                fi.setIsBelowReorder(isBelowReorder);

                factInventoryRepository.save(fi);
                count++;
            } catch (Exception e) {
                log.warn("Skipping inventory log {}: {}", il.getLogId(), e.getMessage());
            }
        }
        return count;
    }
}
