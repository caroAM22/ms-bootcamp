package com.example.resilient_api.infrastructure.adapters.capacityvalidatoradapter.dto;

import lombok.Data;
import java.util.List;

@Data
public class CapacityDataDto {
    private String id;
    private String name;
    private String description;
    private List<TechnologyDto> technologies;
    private Integer techCount;
}