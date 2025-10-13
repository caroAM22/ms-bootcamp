package com.example.resilient_api.infrastructure.adapters.capacityvalidatoradapter.dto;

import lombok.Data;

@Data
public class CapacityResponseDto {
    private String message;
    private CapacityDataDto data;
}