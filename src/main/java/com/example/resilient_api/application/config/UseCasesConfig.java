package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.spi.CapacityValidatorGateway;
import com.example.resilient_api.domain.spi.BootcampCapacityGateway;
import com.example.resilient_api.domain.usecase.BootcampUseCase;
import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.BootcampPersistenceAdapter;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final BootcampRepository bootcampRepository;
        private final BootcampEntityMapper bootcampEntityMapper;

        @Bean
        public BootcampPersistencePort bootcampsPersistencePort() {
                return new BootcampPersistenceAdapter(bootcampRepository,bootcampEntityMapper);
        }

        @Bean
        public BootcampServicePort bootcampServicePort(CapacityValidatorGateway capacityValidatorGateway, BootcampPersistencePort bootcampPersistencePort, BootcampCapacityGateway bootcampCapacityGateway){
                return new BootcampUseCase(capacityValidatorGateway, bootcampPersistencePort, bootcampCapacityGateway);
        }
}
