package com.example.resilient_api.infrastructure.entrypoints.dto;

import lombok.Data;
import java.util.List;

@Data
public class RegistrationRequestDTO {
    private List<String> bootcampIds;
}