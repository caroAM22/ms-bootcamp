package com.example.resilient_api.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    CAPACITIES_VALIDATION_ERROR("400","between 1 and 4 capacities are required", ""),
    INTERNAL_ERROR("500", "Internal server error", ""),
    INVALID_PARAMETERS("400", "Invalid parameters", ""),
    CAPACITY_NOT_FOUND_ERROR("404","One or more capacities do not exist", "");

    private final String code;
    private final String message;
    private final String param;
}