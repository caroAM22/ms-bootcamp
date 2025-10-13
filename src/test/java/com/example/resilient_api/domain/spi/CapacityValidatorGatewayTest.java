package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.Capacity;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class CapacityValidatorGatewayTest {

    @Test
    void validateCapacitiesExist_WhenValidCapacities_ShouldComplete() {
        List<String> capacityIds = Arrays.asList("1", "2");
        
        CapacityValidatorGateway gateway = new CapacityValidatorGateway() {
            @Override
            public Mono<Capacity> validateCapacity(String capacityId) {
                return Mono.just(createValidCapacity());
            }
        };

        StepVerifier.create(gateway.validateCapacitiesExist(capacityIds))
                .verifyComplete();
    }

    @Test
    void validateCapacitiesExist_WhenNullCapacities_ShouldReturnError() {
        CapacityValidatorGateway gateway = new CapacityValidatorGateway() {
            @Override
            public Mono<Capacity> validateCapacity(String capacityId) {
                return Mono.just(createValidCapacity());
            }
        };

        StepVerifier.create(gateway.validateCapacitiesExist(null))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validateCapacitiesExist_WhenEmptyCapacities_ShouldReturnError() {
        List<String> emptyList = List.of();
        CapacityValidatorGateway gateway = new CapacityValidatorGateway() {
            @Override
            public Mono<Capacity> validateCapacity(String capacityId) {
                return Mono.just(createValidCapacity());
            }
        };

        StepVerifier.create(gateway.validateCapacitiesExist(emptyList))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validateCapacitiesExist_WhenTooManyCapacities_ShouldReturnError() {
        List<String> tooMany = Arrays.asList("1", "2", "3", "4", "5");
        CapacityValidatorGateway gateway = new CapacityValidatorGateway() {
            @Override
            public Mono<Capacity> validateCapacity(String capacityId) {
                return Mono.just(createValidCapacity());
            }
        };

        StepVerifier.create(gateway.validateCapacitiesExist(tooMany))
                .expectError(BusinessException.class)
                .verify();
    }

    private Capacity createValidCapacity() {
        Capacity capacity = new Capacity();
        capacity.setId(1);
        capacity.setName("Java");
        capacity.setDescription("Java Programming");
        return capacity;
    }
}