package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.BootcampCapacityGateway;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.spi.CapacityValidatorGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseListTest {

    @Mock
    private CapacityValidatorGateway capacityValidatorGateway;
    
    @Mock
    private BootcampPersistencePort bootcampPersistencePort;
    
    @Mock
    private BootcampCapacityGateway bootcampCapacityGateway;

    private BootcampUseCase bootcampUseCase;

    @BeforeEach
    void setUp() {
        bootcampUseCase = new BootcampUseCase(capacityValidatorGateway, bootcampPersistencePort, bootcampCapacityGateway);
    }

    @Test
    void listBootcamps_ShouldReturnPagedBootcamps() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(10);
        pageRequest.setSortBy("name");
        pageRequest.setDirection(PageRequest.SortDirection.ASC);

        Page<Bootcamp> bootcampPage = createBootcampPage();
        List<BootcampWithCapacities.CapacityWithTechs> capacities = createCapacities();

        when(bootcampPersistencePort.findAll(any())).thenReturn(Mono.just(bootcampPage));
        when(bootcampCapacityGateway.getBootcampCapacities(any())).thenReturn(Mono.just(capacities));

        StepVerifier.create(bootcampUseCase.listBootcamps(pageRequest))
                .expectNextMatches(page -> 
                    page.getContent().size() == 1 &&
                    page.getContent().get(0).getName().equals("Java Bootcamp") &&
                    page.getContent().get(0).getCapacities().size() == 2
                )
                .verifyComplete();
    }

    private Page<Bootcamp> createBootcampPage() {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId("123");
        bootcamp.setName("Java Bootcamp");
        bootcamp.setDescription("Java bootcamp");
        bootcamp.setLaunchDate(LocalDate.now());
        bootcamp.setDuration(12);

        Page<Bootcamp> page = new Page<>();
        page.setContent(List.of(bootcamp));
        page.setPage(0);
        page.setSize(10);
        page.setTotalElements(1);
        page.setTotalPages(1);
        page.setFirst(true);
        page.setLast(true);
        
        return page;
    }

    private List<BootcampWithCapacities.CapacityWithTechs> createCapacities() {
        BootcampWithCapacities.Technology tech1 = new BootcampWithCapacities.Technology();
        tech1.setId("tech1");
        tech1.setName("Java");

        BootcampWithCapacities.CapacityWithTechs capacity1 = new BootcampWithCapacities.CapacityWithTechs();
        capacity1.setId("cap1");
        capacity1.setName("Backend");
        capacity1.setTechnologies(List.of(tech1));
        capacity1.setTechCount(1);

        BootcampWithCapacities.CapacityWithTechs capacity2 = new BootcampWithCapacities.CapacityWithTechs();
        capacity2.setId("cap2");
        capacity2.setName("Frontend");
        capacity2.setTechnologies(List.of());
        capacity2.setTechCount(0);

        return Arrays.asList(capacity1, capacity2);
    }
}