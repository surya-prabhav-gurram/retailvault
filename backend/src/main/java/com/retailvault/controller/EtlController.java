package com.retailvault.controller;

import com.retailvault.dto.ApiResponse;
import com.retailvault.dto.EtlRunLogDto;
import com.retailvault.etl.EtlPipelineService;
import com.retailvault.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/etl")
@RequiredArgsConstructor
public class EtlController {

    private final EtlPipelineService etlPipelineService;
    private final AnalyticsService analyticsService;

    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse<EtlRunLogDto>> triggerEtl(
            @RequestBody(required = false) Map<String, String> body) {
        String triggeredBy = body != null ? body.getOrDefault("triggeredBy", "MANUAL_UI") : "MANUAL_UI";
        log.info("Manual ETL trigger from: {}", triggeredBy);

        // Run async so HTTP response returns immediately
        Thread etlThread = new Thread(() -> etlPipelineService.runFullEtl(triggeredBy));
        etlThread.setName("etl-manual-trigger");
        etlThread.setDaemon(true);
        etlThread.start();

        EtlRunLogDto dto = new EtlRunLogDto();
        dto.setJobName("FULL_ETL");
        dto.setStatus("RUNNING");
        dto.setTriggeredBy(triggeredBy);

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<EtlRunLogDto>>> getEtlHistory() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getEtlHistory()));
    }

    @GetMapping("/status/latest")
    public ResponseEntity<ApiResponse<EtlRunLogDto>> getLatestStatus() {
        List<EtlRunLogDto> history = analyticsService.getEtlHistory();
        if (history.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }
        return ResponseEntity.ok(ApiResponse.ok(history.get(0)));
    }
}
