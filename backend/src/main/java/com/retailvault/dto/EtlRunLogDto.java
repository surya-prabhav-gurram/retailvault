package com.retailvault.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data @AllArgsConstructor @NoArgsConstructor
public class EtlRunLogDto {
    private Long runId;
    private String jobName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer rowsExtracted;
    private Integer rowsLoaded;
    private String errorMessage;
    private String triggeredBy;
    private Long durationSeconds;
}
