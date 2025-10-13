package com.example.resilient_api.infrastructure.entrypoints.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class BootcampDTO {
    private String id;
    private String name;
    private String description;
    private String launchDate;
    private Integer duration;
    private List<String> capacitiesIds;
}
