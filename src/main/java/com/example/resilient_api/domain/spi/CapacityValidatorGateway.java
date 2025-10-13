package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Capacity;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.constants.Constants;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface CapacityValidatorGateway {
    Mono<Capacity> validateCapacity(String capacityId);
    
    default Mono<Void> validateCapacitiesExist(List<String> capacityIds) {
        if (capacityIds == null || capacityIds.isEmpty()) {
            return Mono.error(new BusinessException(TechnicalMessage.CAPACITIES_VALIDATION_ERROR));
        }
        
        if (capacityIds.size() < Constants.MIN_CAPACITIES || capacityIds.size() > Constants.MAX_CAPACITIES) {
            return Mono.error(new BusinessException(TechnicalMessage.CAPACITIES_VALIDATION_ERROR));
        }
        
        return Flux.fromIterable(capacityIds)
                .flatMap(this::validateCapacity)
                .collectList()
                .flatMap(capacities -> {
                    boolean anyNull = capacities.stream().anyMatch(c -> c == null || c.getId() == null);
                    if (anyNull) {
                        return Mono.<Void>error(new BusinessException(TechnicalMessage.CAPACITY_NOT_FOUND_ERROR));
                    }
                    return Mono.<Void>empty();
                });
    }
}