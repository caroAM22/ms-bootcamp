package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.BootcampWithCapacities;
import reactor.core.publisher.Mono;
import java.util.List;

public interface BootcampCapacityGateway {
    Mono<Void> assignCapacitiesToBootcamp(String bootcampId, List<String> capacityIds);
    Mono<List<BootcampWithCapacities.CapacityWithTechs>> getBootcampCapacities(String bootcampId);
    Mono<Void> deleteBootcampCapacities(String bootcampId);
}