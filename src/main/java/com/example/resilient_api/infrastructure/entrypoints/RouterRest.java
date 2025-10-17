package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.infrastructure.entrypoints.handler.BootcampHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(BootcampHandlerImpl bootcampHandler) {
        return route(POST("/bootcamps"), bootcampHandler::createBootcamp)
                .andRoute(GET("/bootcamps"), bootcampHandler::listBootcamps)
                .andRoute(DELETE("/bootcamps/{id}"), bootcampHandler::deleteBootcamp)
                .andRoute(POST("/register"), bootcampHandler::validateRegistration);
    }
}
