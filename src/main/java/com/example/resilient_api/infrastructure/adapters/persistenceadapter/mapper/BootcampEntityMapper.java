package com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import org.springframework.stereotype.Component;

@Component
public class BootcampEntityMapper {
    
    public Bootcamp toModel(BootcampEntity entity) {
        return toModel(entity, null);
    }
    
    public Bootcamp toModel(BootcampEntity entity, java.util.List<String> capacitiesIds) {
        if (entity == null) return null;
        
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(entity.getId());
        bootcamp.setName(entity.getName());
        bootcamp.setDescription(entity.getDescription());
        bootcamp.setLaunchDate(entity.getLaunchDate());
        bootcamp.setDuration(entity.getDuration());
        bootcamp.setCapacitiesIds(capacitiesIds);
        return bootcamp;
    }
    
    public BootcampEntity toEntity(Bootcamp bootcamp) {
        if (bootcamp == null) return null;
        
        BootcampEntity entity = new BootcampEntity();
        entity.setId(bootcamp.getId());
        entity.setName(bootcamp.getName());
        entity.setDescription(bootcamp.getDescription());
        entity.setLaunchDate(bootcamp.getLaunchDate());
        entity.setDuration(bootcamp.getDuration());
        return entity;
    }
}
