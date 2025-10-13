package com.example.resilient_api.infrastructure.adapters.bootcampcapacityadapter;

import com.example.resilient_api.domain.spi.BootcampCapacityGateway;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class BootcampCapacityClient implements BootcampCapacityGateway {
    
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();
    
    @Override
    public Mono<Void> assignCapacitiesToBootcamp(String bootcampId, List<String> capacityIds) {
        return Flux.fromIterable(capacityIds)
                .flatMap(capacityId -> assignCapacityToBootcamp(bootcampId, capacityId))
                .then();
    }
    
    private Mono<Void> assignCapacityToBootcamp(String bootcampId, String capacityId) {
        return webClient.post()
                .uri("/bootcamp-capacity")
                .bodyValue(new BootcampCapacityRequest(bootcampId, capacityId))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> System.err.println("Error assigning capacity " + capacityId + " to bootcamp " + bootcampId + ": " + error.getMessage()));
    }
    
    private record BootcampCapacityRequest(String bootcampId, String capacityId) {}
}