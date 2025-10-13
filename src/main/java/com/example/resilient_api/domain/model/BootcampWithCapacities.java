package com.example.resilient_api.domain.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BootcampWithCapacities {
    private String id;
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer duration;
    private List<CapacityWithTechs> capacities;
    
    @Data
    public static class CapacityWithTechs {
        private String id;
        private String name;
        private String description;
        private List<Technology> technologies;
        private int techCount;
    }
    
    @Data
    public static class Technology {
        private String id;
        private String name;
    }
}