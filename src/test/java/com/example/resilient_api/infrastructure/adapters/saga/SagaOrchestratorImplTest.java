package com.example.resilient_api.infrastructure.adapters.saga;

import com.example.resilient_api.domain.model.SagaStep;
import com.example.resilient_api.domain.model.SagaTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

class SagaOrchestratorImplTest {

    private SagaOrchestratorImpl sagaOrchestrator;

    @BeforeEach
    void setUp() {
        sagaOrchestrator = new SagaOrchestratorImpl();
    }

    @Test
    void executeTransaction_WhenAllStepsSucceed_ShouldComplete() {
        SagaStep step1 = SagaStep.builder()
                .stepName("step1")
                .action(Mono.empty())
                .build();
        
        SagaStep step2 = SagaStep.builder()
                .stepName("step2")
                .action(Mono.empty())
                .build();

        SagaTransaction transaction = SagaTransaction.builder()
                .transactionId("tx-123")
                .bootcampId("bootcamp-123")
                .steps(Arrays.asList(step1, step2))
                .status(SagaTransaction.SagaStatus.STARTED)
                .build();

        StepVerifier.create(sagaOrchestrator.executeTransaction(transaction))
                .verifyComplete();
    }

    @Test
    void executeTransaction_WhenStepFails_ShouldExecuteCompensation() {
        SagaStep step1 = SagaStep.builder()
                .stepName("step1")
                .action(Mono.empty())
                .compensationAction(Mono.empty())
                .build();
        
        SagaStep step2 = SagaStep.builder()
                .stepName("step2")
                .action(Mono.error(new RuntimeException("Step failed")))
                .compensationAction(Mono.empty())
                .build();

        SagaTransaction transaction = SagaTransaction.builder()
                .transactionId("tx-123")
                .bootcampId("bootcamp-123")
                .steps(Arrays.asList(step1, step2))
                .status(SagaTransaction.SagaStatus.STARTED)
                .build();

        StepVerifier.create(sagaOrchestrator.executeTransaction(transaction))
                .expectError(RuntimeException.class)
                .verify();
    }
}