package com.example.resilient_api.infrastructure.adapters.bootcampcapacityadapter;

import com.example.resilient_api.domain.model.BootcampWithCapacities;
import com.example.resilient_api.domain.spi.BootcampCapacityGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BootcampCapacityClient implements BootcampCapacityGateway {
    
    private final WebClient webClient;
    
    @Override
    public Mono<Void> assignCapacitiesToBootcamp(String bootcampId, List<String> capacityIds) {
        return Flux.fromIterable(capacityIds)
                .flatMap(capacityId -> assignCapacityToBootcamp(bootcampId, capacityId))
                .then();
    }
    
    @Override
    public Mono<List<BootcampWithCapacities.CapacityWithTechs>> getBootcampCapacities(String bootcampId) {
        return webClient.get()
                .uri("/bootcamp-capacity/{bootcampId}", bootcampId)
                .retrieve()
                .bodyToMono(BootcampCapacityResponse.class)
                .map(response -> response.data)
                .onErrorReturn(List.of());
    }
    
    @Override
    public Mono<Void> deleteBootcampCapacities(String bootcampId) {
        return webClient.delete()
                .uri("/bootcamp-capacity/{bootcampId}", bootcampId)
                .retrieve()
                .bodyToMono(Void.class);
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
    
    private record BootcampCapacityResponse(String message, List<BootcampWithCapacities.CapacityWithTechs> data) {}
}