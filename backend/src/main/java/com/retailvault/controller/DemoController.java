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
            @RequestParam(defaultValue = "10") int count) {
        if (count < 1 || count > 100) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Count must be between 1 and 100"));
        }
        int created = demoService.generateOrders(count);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
            "ordersCreated", created,
            "message", created + " orders generated. Run ETL to see them in the dashboard."
        )));
    }
}
