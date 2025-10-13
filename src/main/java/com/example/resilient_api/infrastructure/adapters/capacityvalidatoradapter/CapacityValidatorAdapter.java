package com.example.resilient_api.infrastructure.adapters.capacityvalidatoradapter;

import com.example.resilient_api.domain.model.Capacity;
import com.example.resilient_api.domain.spi.CapacityValidatorGateway;
import com.example.resilient_api.infrastructure.adapters.capacityvalidatoradapter.dto.CapacityResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CapacityValidatorAdapter implements CapacityValidatorGateway {
    
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();
    
    @Override
    public Mono<Capacity> validateCapacity(String capacityId) {
        System.out.println("[ADAPTER] Starting validation for capacity: " + capacityId);
        return webClient.get()
                .uri("/capacities/{id}", capacityId)
                .retrieve()
                .bodyToMono(CapacityResponseDto.class)
                .map(response -> {
                    System.out.println("[ADAPTER] Received response for " + capacityId + ": " + (response != null ? "not null" : "null"));
                    if (response != null && response.getData() != null) {
                        System.out.println("[ADAPTER] Response data for " + capacityId + ": " + response.getData().getName());
                        Capacity capacity = new Capacity();
                        capacity.setId(Integer.valueOf(capacityId.hashCode()));
                        capacity.setName(response.getData().getName());
                        capacity.setDescription(response.getData().getDescription());
                        System.out.println("[ADAPTER] Successfully created capacity for " + capacityId);
                        return capacity;
                    }
                    System.out.println("[ADAPTER] Response or data is null for " + capacityId);
                    Capacity emptyCapacity = new Capacity();
                    emptyCapacity.setId(null);
                    return emptyCapacity;
                })
                .onErrorResume(throwable -> {
                    System.out.println("[ADAPTER] Error validating capacity " + capacityId + ": " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage());
                    Capacity emptyCapacity = new Capacity();
                    emptyCapacity.setId(null);
                    return Mono.just(emptyCapacity);
                });
    }
}