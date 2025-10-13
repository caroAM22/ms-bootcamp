package com.example.resilient_api.domain.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class Bootcamp {
    private String id;
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer duration;
    private List<String> capacitiesIds;
}