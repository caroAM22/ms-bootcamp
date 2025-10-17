package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseRegistrationTest {

    @Mock
    private BootcampPersistencePort bootcampPersistencePort;
    
    @InjectMocks
    private BootcampUseCase bootcampUseCase;

    @Test
    void validateRegistration_NoConflicts_ShouldReturnCanRegister() {
        List<String> bootcampIds = List.of("1", "2");
        
        Bootcamp b1 = new Bootcamp();
        b1.setName("Java Bootcamp");
        b1.setLaunchDate(LocalDate.of(2024, 1, 1));
        b1.setDuration(30);
        
        Bootcamp b2 = new Bootcamp();
        b2.setName("React Bootcamp");
        b2.setLaunchDate(LocalDate.of(2024, 3, 1));
        b2.setDuration(30);
        
        when(bootcampPersistencePort.findByIds(bootcampIds))
                .thenReturn(Flux.fromIterable(List.of(b1, b2)));

        StepVerifier.create(bootcampUseCase.validateRegistration(bootcampIds))
                .expectNextMatches(result -> result.isCanRegister() && result.getConflicts().isEmpty())
                .verifyComplete();
    }

    @Test
    void validateRegistration_WithConflicts_ShouldReturnCannotRegister() {
        List<String> bootcampIds = List.of("1", "2");
        
        Bootcamp b1 = new Bootcamp();
        b1.setName("Java Bootcamp");
        b1.setLaunchDate(LocalDate.of(2024, 1, 1));
        b1.setDuration(60);
        
        Bootcamp b2 = new Bootcamp();
        b2.setName("Spring Bootcamp");
        b2.setLaunchDate(LocalDate.of(2024, 1, 15));
        b2.setDuration(30);
        
        when(bootcampPersistencePort.findByIds(bootcampIds))
                .thenReturn(Flux.fromIterable(List.of(b1, b2)));

        StepVerifier.create(bootcampUseCase.validateRegistration(bootcampIds))
                .expectNextMatches(result -> !result.isCanRegister() && !result.getConflicts().isEmpty())
                .verifyComplete();
    }
}