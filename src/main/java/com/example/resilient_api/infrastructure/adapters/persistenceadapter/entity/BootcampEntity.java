package com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

@Table(name = "bootcamps")
@Data
public class BootcampEntity implements Persistable<String> {
    @Id
    private String id;
    private String name;
    private String description;
    @Column("launch_date")
    private LocalDate launchDate;
    private Integer duration;
    
    @Override
    public boolean isNew() {
        return true;
    }
}
