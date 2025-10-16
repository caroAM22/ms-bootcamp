package com.example.resilient_api.domain.model;

import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
@Builder
public class SagaStep {
    private String stepName;
    private Mono<Void> action;
    private Mono<Void> compensationAction;
    private boolean executed;
    private boolean compensated;
}