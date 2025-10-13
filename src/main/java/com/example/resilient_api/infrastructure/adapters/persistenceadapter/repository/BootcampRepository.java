package com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository;

import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, String> {
}