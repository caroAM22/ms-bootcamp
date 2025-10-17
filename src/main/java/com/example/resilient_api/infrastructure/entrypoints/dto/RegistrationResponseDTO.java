package com.example.resilient_api.infrastructure.entrypoints.dto;

import lombok.Data;
import java.util.List;

@Data
public class RegistrationResponseDTO {
    private boolean canRegister;
    private String message;
    private List<String> conflicts;
}