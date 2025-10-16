package com.example.resilient_api.domain.spi;

import reactor.core.publisher.Mono;
import java.util.List;

public interface CapacitySagaGateway {
    Mono<List<String>> getCapacitiesByBootcamp(String bootcampId);
    Mono<Void> deleteOrphanCapacities(String bootcampId, List<String> capacityIds);
    Mono<Void> restoreCapacities(List<String> capacityIds);
}