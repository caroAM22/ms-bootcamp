package com.example.resilient_api.domain.model;

import lombok.Data;
import java.util.List;

@Data
public class RegistrationValidationResult {
    private boolean canRegister;
    private String message;
    private List<String> conflicts;
}