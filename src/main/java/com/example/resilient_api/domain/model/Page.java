package com.example.resilient_api.domain.model;

import lombok.Data;
import java.util.List;

@Data
public class Page<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}