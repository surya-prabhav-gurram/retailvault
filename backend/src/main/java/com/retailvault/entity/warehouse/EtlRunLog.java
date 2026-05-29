package com.retailvault.entity.warehouse;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "etl_run_log", catalog = "retailvault_warehouse")
public class EtlRunLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "job_name")
    private String jobName;

    private String status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "rows_extracted")
    private Integer rowsExtracted;

    @Column(name = "rows_loaded")
    private Integer rowsLoaded;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "triggered_by")
    private String triggeredBy;
}
