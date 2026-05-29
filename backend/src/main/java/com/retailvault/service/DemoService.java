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

    @Transactional("oltpTransactionManager")
    public int generateOrders(int count) {
        List<Store> stores = storeOltpRepository.findAll();
        List<Product> products = productOltpRepository.findAll();
        List<Customer> customers = customerOltpRepository.findAll();

        if (stores.isEmpty() || products.isEmpty() || customers.isEmpty()) {
            throw new RuntimeException("No stores, products, or customers found in OLTP");
        }

        Random rnd = new Random();
        int ordersCreated = 0;

        for (int i = 0; i < count; i++) {
            Store store = stores.get(rnd.nextInt(stores.size()));
            Customer customer = customers.get(rnd.nextInt(customers.size()));

            Order order = new Order();
            order.setStore(store);
            order.setCustomer(customer);
            order.setOrderDate(LocalDateTime.now().minusDays(rnd.nextInt(30)));
            order.setStatus("COMPLETED");

            int itemCount = 1 + rnd.nextInt(3);
            BigDecimal total = BigDecimal.ZERO;

            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(rnd.nextInt(products.size()));
                int qty = 1 + rnd.nextInt(5);
                BigDecimal price = product.getUnitPrice();
                BigDecimal discount = rnd.nextInt(10) < 3
                    ? BigDecimal.valueOf(rnd.nextInt(20) + 5)
                    : BigDecimal.ZERO;
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
            }

            order.setTotalAmount(total);
            ordersCreated++;
        }

        log.info("Generated {} demo orders", ordersCreated);
        return ordersCreated;
    }
}
