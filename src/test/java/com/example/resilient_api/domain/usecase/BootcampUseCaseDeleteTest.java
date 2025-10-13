package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.spi.BootcampCapacityGateway;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseDeleteTest {

    @Mock
    private BootcampCapacityGateway bootcampCapacityGateway;
    
    @Mock
    private BootcampPersistencePort bootcampPersistencePort;
    
    @InjectMocks
    private BootcampUseCase bootcampUseCase;

    @Test
    void deleteBootcamp_ShouldDeleteCapacitiesAndBootcamp() {
        String bootcampId = "test-id";
        
        when(bootcampCapacityGateway.deleteBootcampCapacities(bootcampId))
                .thenReturn(Mono.empty());
        when(bootcampPersistencePort.deleteById(bootcampId))
                .thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.deleteBootcamp(bootcampId))
                .verifyComplete();
    }
}