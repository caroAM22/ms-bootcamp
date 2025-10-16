package com.example.resilient_api.domain.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SagaTransaction {
    private String transactionId;
    private String bootcampId;
    private List<SagaStep> steps;
    private SagaStatus status;
    
    public enum SagaStatus {
        STARTED, IN_PROGRESS, COMPLETED, FAILED, COMPENSATING, COMPENSATED
    }
}