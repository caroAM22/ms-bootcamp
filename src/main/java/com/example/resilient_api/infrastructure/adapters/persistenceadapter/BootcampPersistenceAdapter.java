package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.Page;
import com.example.resilient_api.domain.model.PageRequest;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

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
    
    @Override
    public Mono<Page<Bootcamp>> findAll(PageRequest pageRequest) {
        int offset = pageRequest.getPage() * pageRequest.getSize();
        return bootcampRepository.count()
                .flatMap(totalElements -> {
                    return bootcampRepository.findAllWithPagination(pageRequest.getSize(), offset)
                            .map(entity -> bootcampEntityMapper.toModel(entity))
                            .collectList()
                            .map(bootcamps -> {
                                // Sort in memory based on pageRequest
                                bootcamps.sort((b1, b2) -> {
                                    int comparison = 0;
                                    if ("name".equals(pageRequest.getSortBy())) {
                                        comparison = b1.getName().compareTo(b2.getName());
                                    }
                                    return pageRequest.getDirection() == PageRequest.SortDirection.DESC ? -comparison : comparison;
                                });
                                
                                Page<Bootcamp> page = new Page<>();
                                page.setContent(bootcamps);
                                page.setPage(pageRequest.getPage());
                                page.setSize(pageRequest.getSize());
                                page.setTotalElements(totalElements);
                                page.setTotalPages((int) Math.ceil((double) totalElements / pageRequest.getSize()));
                                page.setFirst(pageRequest.getPage() == 0);
                                page.setLast(pageRequest.getPage() >= page.getTotalPages() - 1);
                                return page;
                            });
                });
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return bootcampRepository.deleteById(id);
    }
    
    @Override
    public Mono<Bootcamp> findById(String id) {
        return bootcampRepository.findById(id)
                .map(bootcampEntityMapper::toModel);
    }
    
    @Override
    public Flux<Bootcamp> findByIds(List<String> ids) {
        return bootcampRepository.findAllById(ids)
                .map(bootcampEntityMapper::toModel);
    }
}
