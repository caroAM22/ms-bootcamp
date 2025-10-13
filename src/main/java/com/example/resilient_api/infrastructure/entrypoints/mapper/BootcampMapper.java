package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class BootcampMapper {
    
    public Bootcamp bootcampDTOTobBootcamp(BootcampDTO bootcampDTO) {
        if (bootcampDTO == null) return null;
        
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setName(bootcampDTO.getName());
        bootcamp.setDescription(bootcampDTO.getDescription());
        bootcamp.setLaunchDate(bootcampDTO.getLaunchDate() != null ? LocalDate.parse(bootcampDTO.getLaunchDate()) : null);
        bootcamp.setDuration(bootcampDTO.getDuration());
        bootcamp.setCapacitiesIds(bootcampDTO.getCapacitiesIds());
        return bootcamp;
    }
}
