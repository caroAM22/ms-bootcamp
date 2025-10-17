package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class BootcampUseCase implements BootcampServicePort{
    
    private final CapacityValidatorGateway capacityValidatorGateway;
    private final BootcampPersistencePort bootcampPersistencePort;
    private final BootcampCapacityGateway bootcampCapacityGateway;
    private final SagaOrchestrator sagaOrchestrator;
    private final CapacitySagaGateway capacitySagaGateway;
    
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
    
    public Mono<Page<BootcampWithCapacities>> listBootcamps(PageRequest pageRequest) {
        return bootcampPersistencePort.findAll(pageRequest)
                .flatMap(bootcampPage -> {
                    return Flux.fromIterable(bootcampPage.getContent())
                            .flatMap(this::enrichBootcampWithCapacities)
                            .collectList()
                            .map(enrichedBootcamps -> {
                                Page<BootcampWithCapacities> result = new Page<>();
                                result.setContent(enrichedBootcamps);
                                result.setPage(bootcampPage.getPage());
                                result.setSize(bootcampPage.getSize());
                                result.setTotalElements(bootcampPage.getTotalElements());
                                result.setTotalPages(bootcampPage.getTotalPages());
                                result.setFirst(bootcampPage.isFirst());
                                result.setLast(bootcampPage.isLast());
                                return result;
                            });
                });
    }
    
    public Mono<Void> deleteBootcamp(String bootcampId) {
        return createDeleteBootcampSaga(bootcampId)
                .flatMap(sagaOrchestrator::executeTransaction);
    }
    
    private Mono<SagaTransaction> createDeleteBootcampSaga(String bootcampId) {
        String transactionId = UUID.randomUUID().toString();
        
        return capacitySagaGateway.getCapacitiesByBootcamp(bootcampId)
                .onErrorReturn(List.of())
                .map(capacityIds -> {
                    log.info("Creating saga for bootcamp {} with {} capacities", bootcampId, capacityIds.size());
                    
                    List<SagaStep> steps = Arrays.asList(
                        SagaStep.builder()
                            .stepName("delete-orphan-capacities")
                            .action(capacityIds.isEmpty() ? 
                                Mono.<Void>empty().doOnSuccess(v -> log.info("No capacities to delete for bootcamp {}", bootcampId)) :
                                capacitySagaGateway.deleteOrphanCapacities(bootcampId, capacityIds)
                                    .doOnSuccess(v -> log.info("Deleted orphan capacities for {}", bootcampId))
                                    .doOnError(e -> log.error("Failed to delete orphan capacities for {}", bootcampId, e)))
                            .compensationAction(Mono.empty())
                            .build(),
                        SagaStep.builder()
                            .stepName("delete-bootcamp-capacities")
                            .action(bootcampCapacityGateway.deleteBootcampCapacities(bootcampId)
                                .doOnSuccess(v -> log.info("Deleted bootcamp-capacity relations for {}", bootcampId))
                                .doOnError(e -> log.error("Failed to delete bootcamp-capacity relations for {}", bootcampId, e)))
                            .compensationAction(Mono.empty())
                            .build(),
                        SagaStep.builder()
                            .stepName("delete-bootcamp")
                            .action(bootcampPersistencePort.deleteById(bootcampId)
                                .doOnSuccess(v -> log.info("Deleted bootcamp {}", bootcampId))
                                .doOnError(e -> log.error("Failed to delete bootcamp {}", bootcampId, e)))
                            .compensationAction(restoreBootcamp(bootcampId))
                            .build()
                    );
                    
                    return SagaTransaction.builder()
                        .transactionId(transactionId)
                        .bootcampId(bootcampId)
                        .steps(steps)
                        .status(SagaTransaction.SagaStatus.STARTED)
                        .build();
                });
    }
    
    
    private Mono<Void> restoreBootcamp(String bootcampId) {
        log.warn("Bootcamp restoration not implemented for id: {}", bootcampId);
        return Mono.empty();
    }
    
    public Mono<RegistrationValidationResult> validateRegistration(List<String> bootcampIds) {
        return bootcampPersistencePort.findByIds(bootcampIds)
                .collectList()
                .map(bootcamps -> {
                    RegistrationValidationResult result = new RegistrationValidationResult();
                    
                    // Check if all bootcamps exist
                    if (bootcamps.size() != bootcampIds.size()) {
                        result.setCanRegister(false);
                        result.setMessage("Some bootcamps do not exist");
                        result.setConflicts(List.of("Invalid bootcamp IDs provided"));
                        return result;
                    }
                    
                    List<String> conflicts = new ArrayList<>();
                    
                    for (int i = 0; i < bootcamps.size(); i++) {
                        for (int j = i + 1; j < bootcamps.size(); j++) {
                            Bootcamp b1 = bootcamps.get(i);
                            Bootcamp b2 = bootcamps.get(j);
                            
                            if (hasDateConflict(b1, b2)) {
                                conflicts.add(b1.getName() + " conflicts with " + b2.getName());
                            }
                        }
                    }
                    
                    result.setCanRegister(conflicts.isEmpty());
                    result.setConflicts(conflicts);
                    result.setMessage(conflicts.isEmpty() ? "Registration allowed" : "Date conflicts found");
                    return result;
                });
    }
    
    private boolean hasDateConflict(Bootcamp b1, Bootcamp b2) {
        LocalDate end1 = b1.getLaunchDate().plusDays(b1.getDuration());
        LocalDate end2 = b2.getLaunchDate().plusDays(b2.getDuration());
        
        return !(b1.getLaunchDate().isAfter(end2) || b2.getLaunchDate().isAfter(end1));
    }
    
    private Mono<BootcampWithCapacities> enrichBootcampWithCapacities(Bootcamp bootcamp) {
        return bootcampCapacityGateway.getBootcampCapacities(bootcamp.getId())
                .map(capacities -> {
                    BootcampWithCapacities enriched = new BootcampWithCapacities();
                    enriched.setId(bootcamp.getId());
                    enriched.setName(bootcamp.getName());
                    enriched.setDescription(bootcamp.getDescription());
                    enriched.setLaunchDate(bootcamp.getLaunchDate());
                    enriched.setDuration(bootcamp.getDuration());
                    enriched.setCapacities(capacities);
                    return enriched;
                });
    }
}