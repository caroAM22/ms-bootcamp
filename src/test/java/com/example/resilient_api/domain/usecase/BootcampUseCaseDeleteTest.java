package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.SagaTransaction;
import com.example.resilient_api.domain.spi.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BootcampUseCaseDeleteTest {

    @Mock
    private CapacityValidatorGateway capacityValidatorGateway;
    @Mock
    private BootcampPersistencePort bootcampPersistencePort;
    @Mock
    private BootcampCapacityGateway bootcampCapacityGateway;
    @Mock
    private SagaOrchestrator sagaOrchestrator;
    @Mock
    private CapacitySagaGateway capacitySagaGateway;

    private BootcampUseCase bootcampUseCase;

    @BeforeEach
    void setUp() {
        bootcampUseCase = new BootcampUseCase(capacityValidatorGateway, bootcampPersistencePort, 
                bootcampCapacityGateway, sagaOrchestrator, capacitySagaGateway);
    }

    @Test
    void deleteBootcamp_ShouldExecuteSagaSuccessfully() {
        String bootcampId = "bootcamp-123";
        
        when(capacitySagaGateway.getCapacitiesByBootcamp(bootcampId))
                .thenReturn(Mono.just(Arrays.asList("cap1", "cap2")));
        when(bootcampCapacityGateway.deleteBootcampCapacities(bootcampId))
                .thenReturn(Mono.empty());
        when(capacitySagaGateway.deleteOrphanCapacities(bootcampId, Arrays.asList("cap1", "cap2")))
                .thenReturn(Mono.empty());
        when(bootcampPersistencePort.deleteById(bootcampId))
                .thenReturn(Mono.empty());
        when(sagaOrchestrator.executeTransaction(any(SagaTransaction.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.deleteBootcamp(bootcampId))
                .verifyComplete();
    }

    @Test
    void deleteBootcamp_WhenSagaFails_ShouldPropagateError() {
        String bootcampId = "bootcamp-123";
        
        when(capacitySagaGateway.getCapacitiesByBootcamp(bootcampId))
                .thenReturn(Mono.just(Arrays.asList("cap1", "cap2")));
        when(sagaOrchestrator.executeTransaction(any(SagaTransaction.class)))
                .thenReturn(Mono.error(new RuntimeException("Saga failed")));

        StepVerifier.create(bootcampUseCase.deleteBootcamp(bootcampId))
                .expectError(RuntimeException.class)
                .verify();
    }
}