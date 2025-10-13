package com.example.resilient_api.domain.spi;

import reactor.core.publisher.Mono;
import java.util.List;

public interface BootcampCapacityGateway {
    Mono<Void> assignCapacitiesToBootcamp(String bootcampId, List<String> capacityIds);
}