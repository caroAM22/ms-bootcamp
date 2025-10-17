package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.BootcampWithCapacities;
import com.example.resilient_api.domain.model.Page;
import com.example.resilient_api.domain.model.PageRequest;
import com.example.resilient_api.domain.model.RegistrationValidationResult;
import reactor.core.publisher.Mono;
import java.util.List;

public interface BootcampServicePort {
    Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp);
    Mono<Page<BootcampWithCapacities>> listBootcamps(PageRequest pageRequest);
    Mono<Void> deleteBootcamp(String bootcampId);
    Mono<RegistrationValidationResult> validateRegistration(List<String> bootcampIds);
}
