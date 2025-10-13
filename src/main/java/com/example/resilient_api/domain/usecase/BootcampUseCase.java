package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.spi.CapacityValidatorGateway;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.spi.BootcampCapacityGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class BootcampUseCase implements BootcampServicePort{
    
    private final CapacityValidatorGateway capacityValidatorGateway;
    private final BootcampPersistencePort bootcampPersistencePort;
    private final BootcampCapacityGateway bootcampCapacityGateway;
    
    public Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp) {
        log.info("Starting bootcamp registration: name={}, capacitiesIds={}", bootcamp.getName(), bootcamp.getCapacitiesIds());
        log.info("CapacitiesIds count: {}", bootcamp.getCapacitiesIds() != null ? bootcamp.getCapacitiesIds().size() : "null");
        
        return capacityValidatorGateway.validateCapacitiesExist(bootcamp.getCapacitiesIds())
                .doOnSuccess(v -> log.info("Capacities validation successful"))
                .doOnError(e -> log.error("Capacities validation failed: {}", e.getMessage()))
                .then(Mono.defer(() -> {
                    bootcamp.setId(UUID.randomUUID().toString()); 
                    log.info("Saving bootcamp with ID: {}", bootcamp.getId());
                    return bootcampPersistencePort.save(bootcamp);
                }))
                .flatMap(savedBootcamp -> {
                    log.info("Assigning capacities to bootcamp: {}", savedBootcamp.getId());
                    return bootcampCapacityGateway.assignCapacitiesToBootcamp(
                            savedBootcamp.getId(), 
                            savedBootcamp.getCapacitiesIds()
                    ).thenReturn(savedBootcamp);
                });
    }
}