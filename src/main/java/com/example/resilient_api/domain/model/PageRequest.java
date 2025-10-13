package com.example.resilient_api.domain.model;

import lombok.Data;

@Data
public class PageRequest {
    private int page;
    private int size;
    private String sortBy;
    private SortDirection direction;
    
    public enum SortDirection {
        ASC, DESC
    }
}