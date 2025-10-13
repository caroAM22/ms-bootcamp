package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.Page;
import com.example.resilient_api.domain.model.PageRequest;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
    Mono<Bootcamp> save(Bootcamp user);
    Mono<Page<Bootcamp>> findAll(PageRequest pageRequest);
    Mono<Void> deleteById(String id);
}
