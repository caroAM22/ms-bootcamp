package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.spi.CapacityValidatorGateway;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.spi.BootcampCapacityGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @Mock
    private CapacityValidatorGateway capacityValidatorGateway;
    
    @Mock
    private BootcampPersistencePort bootcampPersistencePort;
    
    @Mock
    private BootcampCapacityGateway bootcampCapacityGateway;

    private BootcampUseCase bootcampUseCase;

    @BeforeEach
    void setUp() {
        bootcampUseCase = new BootcampUseCase(capacityValidatorGateway, bootcampPersistencePort, bootcampCapacityGateway);
    }

    @Test
    void registerBootcamp_WhenValidCapacities_ShouldReturnBootcamp() {
        Bootcamp bootcamp = createValidBootcamp();
        Bootcamp savedBootcamp = createValidBootcamp();
        savedBootcamp.setId(UUID.randomUUID().toString());
        
        when(capacityValidatorGateway.validateCapacitiesExist(any()))
                .thenReturn(Mono.empty());
        when(bootcampPersistencePort.save(any()))
                .thenReturn(Mono.just(savedBootcamp));
        when(bootcampCapacityGateway.assignCapacitiesToBootcamp(any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.registerBootcamp(bootcamp))
                .expectNext(savedBootcamp)
                .verifyComplete();
    }

    @Test
    void registerBootcamp_WhenInvalidCapacities_ShouldReturnError() {
        Bootcamp bootcamp = createValidBootcamp();
        when(capacityValidatorGateway.validateCapacitiesExist(any()))
                .thenReturn(Mono.error(new RuntimeException("Invalid capacities")));

        StepVerifier.create(bootcampUseCase.registerBootcamp(bootcamp))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void registerBootcamp_WhenEmptyCapacities_ShouldReturnError() {
        Bootcamp bootcamp = createValidBootcamp();
        bootcamp.setCapacitiesIds(List.of());
        when(capacityValidatorGateway.validateCapacitiesExist(any()))
                .thenReturn(Mono.error(new RuntimeException("Las capacidades no pueden estar vacías")));

        StepVerifier.create(bootcampUseCase.registerBootcamp(bootcamp))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void registerBootcamp_WhenTooManyCapacities_ShouldReturnError() {
        Bootcamp bootcamp = createValidBootcamp();
        bootcamp.setCapacitiesIds(Arrays.asList("1", "2", "3", "4", "5"));
        when(capacityValidatorGateway.validateCapacitiesExist(any()))
                .thenReturn(Mono.error(new RuntimeException("Debe tener entre 1 y 4 capacidades")));

        StepVerifier.create(bootcampUseCase.registerBootcamp(bootcamp))
                .expectError(RuntimeException.class)
                .verify();
    }

    private Bootcamp createValidBootcamp() {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setName("Java Bootcamp");
        bootcamp.setDescription("Bootcamp de Java");
        bootcamp.setLaunchDate(LocalDate.now().plusDays(30));
        bootcamp.setDuration(12);
        bootcamp.setCapacitiesIds(Arrays.asList("1", "2"));
        return bootcamp;
    }
}