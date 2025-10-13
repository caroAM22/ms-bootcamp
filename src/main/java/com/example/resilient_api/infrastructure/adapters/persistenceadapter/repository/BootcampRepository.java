package com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository;

import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, String> {
    
    @Query("SELECT * FROM bootcamps LIMIT :limit OFFSET :offset")
    Flux<BootcampEntity> findAllWithPagination(int limit, int offset);
}