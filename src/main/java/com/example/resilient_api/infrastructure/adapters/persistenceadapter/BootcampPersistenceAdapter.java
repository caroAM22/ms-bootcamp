package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class BootcampPersistenceAdapter implements BootcampPersistencePort {
    private final BootcampRepository bootcampRepository;
    private final BootcampEntityMapper bootcampEntityMapper;

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        log.info("[PERSISTENCE] Starting to save bootcamp: {}", bootcamp.getName());
        BootcampEntity entity = bootcampEntityMapper.toEntity(bootcamp);
        log.info("[PERSISTENCE] Mapped to entity: id={}, name={}", 
                entity.getId(), entity.getName());
        
        return bootcampRepository.save(entity)
                .doOnSuccess(savedEntity -> log.info("[PERSISTENCE] Successfully saved entity with id: {}", savedEntity.getId()))
                .doOnError(error -> log.error("[PERSISTENCE] Error saving entity: {}", error.getMessage()))
                .map(savedEntity -> {
                    Bootcamp savedBootcamp = bootcampEntityMapper.toModel(savedEntity, bootcamp.getCapacitiesIds());
                    log.info("[PERSISTENCE] Mapped back to domain: {}", savedBootcamp.getName());
                    return savedBootcamp;
                });
    }

}
