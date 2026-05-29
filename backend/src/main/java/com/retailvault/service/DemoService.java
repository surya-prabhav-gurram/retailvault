package com.retailvault.service;

import com.retailvault.entity.oltp.*;
import com.retailvault.repository.oltp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoService {

    private final OrderItemRepository orderItemRepository;
    private final StoreOltpRepository storeOltpRepository;
    private final ProductOltpRepository productOltpRepository;
    private final CustomerOltpRepository customerOltpRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final OrderOltpRepository orderOltpRepository;

    @Transactional("oltpTransactionManager")
    public int generateOrders(int count, String scenario) {
        List<Store> stores = storeOltpRepository.findAll();
        List<Product> products = productOltpRepository.findAll();
        List<Customer> customers = customerOltpRepository.findAll();

        if (stores.isEmpty() || products.isEmpty() || customers.isEmpty()) {
            throw new RuntimeException("No stores, products, or customers found in OLTP");
        }

        Random rnd = new Random();
        boolean isBlackFriday = "BLACK_FRIDAY".equals(scenario);
        int ordersCreated = 0;

        for (int i = 0; i < count; i++) {
            Store store = stores.get(rnd.nextInt(stores.size()));
            Customer customer = customers.get(rnd.nextInt(customers.size()));

            Order order = new Order();
            order.setStore(store);
            order.setCustomer(customer);
            int daysBack = isBlackFriday ? rnd.nextInt(3) : rnd.nextInt(30);
            order.setOrderDate(LocalDateTime.now().minusDays(daysBack));
            order.setStatus("COMPLETED");
            order.setTotalAmount(BigDecimal.ZERO);
            order = orderOltpRepository.save(order);

            int itemCount = isBlackFriday ? 2 + rnd.nextInt(4) : 1 + rnd.nextInt(3);
            BigDecimal total = BigDecimal.ZERO;

            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(rnd.nextInt(products.size()));
                int qty = isBlackFriday ? 3 + rnd.nextInt(8) : 1 + rnd.nextInt(5);
                BigDecimal price = product.getUnitPrice();
                BigDecimal discount = isBlackFriday
                    ? BigDecimal.valueOf(15 + rnd.nextInt(25))
                    : (rnd.nextInt(10) < 3 ? BigDecimal.valueOf(rnd.nextInt(20) + 5) : BigDecimal.ZERO);
                BigDecimal gross = price.multiply(BigDecimal.valueOf(qty));
                BigDecimal discAmt = gross.multiply(discount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal lineTotal = gross.subtract(discAmt);

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setQuantity(qty);
                item.setUnitPrice(price);
                item.setDiscount(discount);
                item.setLineTotal(lineTotal);
                orderItemRepository.save(item);
                total = total.add(lineTotal);

                // Write inventory log for stock decrease
                int stockBefore = 20 + rnd.nextInt(30);
                int stockAfter = Math.max(0, stockBefore - qty);
                InventoryLog invLog = new InventoryLog();
                invLog.setStore(store);
                invLog.setProduct(product);
                invLog.setMovementType("SALE");
                invLog.setQuantity(qty);
                invLog.setStockBefore(stockBefore);
                invLog.setStockAfter(stockAfter);
                invLog.setMovementDate(order.getOrderDate());
                inventoryLogRepository.save(invLog);
            }

            order.setTotalAmount(total);
            orderOltpRepository.save(order);
            ordersCreated++;
        }

        log.info("Generated {} {} orders", ordersCreated, scenario);
        return ordersCreated;
    }

    @Transactional("oltpTransactionManager")
    public int restockInventory() {
        List<Store> stores = storeOltpRepository.findAll();
        List<Product> products = productOltpRepository.findAll();
        Random rnd = new Random();
        int count = 0;

        for (Store store : stores) {
            for (Product product : products) {
                int restockQty = 20 + rnd.nextInt(30);
                InventoryLog log2 = new InventoryLog();
                log2.setStore(store);
                log2.setProduct(product);
                log2.setMovementType("RESTOCK");
                log2.setQuantity(restockQty);
                log2.setStockBefore(rnd.nextInt(10));
                log2.setStockAfter(log2.getStockBefore() + restockQty);
                log2.setMovementDate(LocalDateTime.now());
                inventoryLogRepository.save(log2);
                count++;
            }
        }
        log.info("Restocked {} product-store combinations", count);
        return count;
    }
}
