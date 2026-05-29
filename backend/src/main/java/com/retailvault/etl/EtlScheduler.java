package com.retailvault.etl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtlScheduler {

    private final EtlPipelineService etlPipelineService;

    @Value("${etl.schedule.enabled:true}")
    private boolean schedulingEnabled;

    /**
     * Runs the full ETL pipeline on the configured cron schedule.
     * Default: every day at 2:00 AM
     */
    @Scheduled(cron = "${etl.schedule.cron:0 0 2 * * *}")
    public void scheduledEtlRun() {
        if (!schedulingEnabled) {
            log.info("ETL scheduling is disabled, skipping scheduled run.");
            return;
        }
        log.info("Scheduled ETL triggered at: {}", java.time.LocalDateTime.now());
        etlPipelineService.runFullEtl("SCHEDULER");
    }
}
