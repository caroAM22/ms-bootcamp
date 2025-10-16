package com.example.resilient_api.infrastructure.adapters.saga;

import com.example.resilient_api.domain.spi.CapacitySagaGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacitySagaAdapter implements CapacitySagaGateway {

    private final WebClient webClient;

    @Override
    @SuppressWarnings("unchecked")
    public Mono<List<String>> getCapacitiesByBootcamp(String bootcampId) {
        return webClient.get()
                .uri("/saga/capacities/bootcamp/{bootcampId}", bootcampId)
                .retrieve()
                .bodyToMono(List.class)
                .map(list -> (List<String>) list)
                .doOnSuccess(capacities -> log.info("Retrieved {} capacities for bootcamp {}", capacities.size(), bootcampId))
                .onErrorResume(e -> {
                    log.error("Error getting capacities for bootcamp {}: {}", bootcampId, e.getMessage());
                    return Mono.just(List.of());
                });
    }

    @Override
    public Mono<Void> deleteOrphanCapacities(String bootcampId, List<String> capacityIds) {
        log.info("Sending DELETE request to /saga/capacities/orphan/{} with body: {}", bootcampId, capacityIds);
        return webClient.method(HttpMethod.DELETE)
                .uri("/saga/capacities/orphan/{bootcampId}", bootcampId)
                .bodyValue(capacityIds)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Deleted orphan capacities for bootcamp {}", bootcampId))
                .doOnError(e -> log.error("Error deleting orphan capacities for bootcamp {}: {}", bootcampId, e.getMessage()));
    }

    @Override
    public Mono<Void> restoreCapacities(List<String> capacityIds) {
        return webClient.post()
                .uri("/saga/capacities/restore")
                .bodyValue(capacityIds)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Restored capacities: {}", capacityIds));
    }
}