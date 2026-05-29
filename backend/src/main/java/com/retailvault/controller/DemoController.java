package com.retailvault.controller;

import com.retailvault.dto.ApiResponse;
import com.retailvault.service.DemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @PostMapping("/generate-orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateOrders(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "NORMAL") String scenario) {
        if (count < 1 || count > 200) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Count must be between 1 and 200"));
        }
        int created = demoService.generateOrders(count, scenario);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
            "ordersCreated", created,
            "scenario", scenario,
            "message", created + " orders generated. Run ETL to see them in the dashboard."
        )));
    }

    @PostMapping("/restock-inventory")
    public ResponseEntity<ApiResponse<Map<String, Object>>> restockInventory() {
        int count = demoService.restockInventory();
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
            "restockedEntries", count,
            "message", count + " inventory entries restocked. Run ETL to update alerts."
        )));
    }
}
