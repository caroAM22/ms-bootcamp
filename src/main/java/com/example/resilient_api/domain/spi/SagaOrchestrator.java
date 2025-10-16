package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.SagaTransaction;
import reactor.core.publisher.Mono;

public interface SagaOrchestrator {
    Mono<Void> executeTransaction(SagaTransaction transaction);
}