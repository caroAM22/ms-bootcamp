package com.example.resilient_api.infrastructure.adapters.saga;

import com.example.resilient_api.domain.model.SagaStep;
import com.example.resilient_api.domain.model.SagaTransaction;
import com.example.resilient_api.domain.spi.SagaOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collections;

@Component
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator {

    @Override
    public Mono<Void> executeTransaction(SagaTransaction transaction) {
        log.info("Starting saga transaction: {}", transaction.getTransactionId());
        
        return Flux.fromIterable(transaction.getSteps())
                .concatMap(this::executeStep)
                .then()
                .doOnSuccess(v -> log.info("Saga completed successfully: {}", transaction.getTransactionId()))
                .onErrorResume(error -> {
                    log.error("Saga failed, starting compensation: {}", transaction.getTransactionId(), error);
                    return compensateTransaction(transaction).then(Mono.error(error));
                });
    }

    private Mono<Void> executeStep(SagaStep step) {
        log.info("Executing step: {}", step.getStepName());
        return step.getAction()
                .doOnSuccess(v -> {
                    step.setExecuted(true);
                    log.info("Step completed: {}", step.getStepName());
                })
                .doOnError(e -> log.error("Step failed: {}", step.getStepName(), e));
    }

    private Mono<Void> compensateTransaction(SagaTransaction transaction) {
        log.info("Starting compensation for transaction: {}", transaction.getTransactionId());
        
        return Flux.fromIterable(transaction.getSteps())
                .filter(SagaStep::isExecuted)
                .collectList()
                .map(steps -> {
                    Collections.reverse(steps);
                    return steps;
                })
                .flatMapMany(Flux::fromIterable)
                .concatMap(this::compensateStep)
                .then()
                .doOnSuccess(v -> log.info("Compensation completed: {}", transaction.getTransactionId()));
    }

    private Mono<Void> compensateStep(SagaStep step) {
        if (step.getCompensationAction() == null) {
            log.warn("No compensation action for step: {}", step.getStepName());
            return Mono.empty();
        }
        
        log.info("Compensating step: {}", step.getStepName());
        return step.getCompensationAction()
                .doOnSuccess(v -> {
                    step.setCompensated(true);
                    log.info("Step compensated: {}", step.getStepName());
                })
                .doOnError(e -> log.error("Compensation failed for step: {}", step.getStepName(), e));
    }
}