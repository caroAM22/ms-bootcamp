package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.exceptions.TechnicalException;
import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.PageRequest;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampDTO;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampMapper;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampHandlerImpl {

    private final BootcampServicePort bootcampServicePort;
    private final BootcampMapper bootcampMapper;

    public Mono<ServerResponse> listBootcamps(ServerRequest request) {
        String messageId = getMessageId(request);
        
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String direction = request.queryParam("direction").orElse("ASC");
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSortBy(sortBy);
        pageRequest.setDirection(PageRequest.SortDirection.valueOf(direction.toUpperCase()));
        
        return bootcampServicePort.listBootcamps(pageRequest)
                .flatMap(bootcampPage -> ServerResponse
                        .ok()
                        .bodyValue(bootcampPage))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("[{}] Unexpected error: {}", messageId, ex.getMessage(), ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code("500")
                                    .message("Internal server error: " + ex.getMessage())
                                    .build()));
                });
    }
    
    public Mono<ServerResponse> createBootcamp(ServerRequest request) {
        String messageId = getMessageId(request);
        return request.bodyToMono(BootcampDTO.class)
                .doOnNext(dto -> {
                    log.info("[{}] Received BootcampDTO: name={}, description={}, launchDate={}, duration={}, capacitiesIds={}", 
                            messageId, dto.getName(), dto.getDescription(), dto.getLaunchDate(), dto.getDuration(), dto.getCapacitiesIds());
                    log.info("[{}] CapacitiesIds size: {}", messageId, dto.getCapacitiesIds() != null ? dto.getCapacitiesIds().size() : "null");
                })
                .flatMap((BootcampDTO bootcampDTO) -> {
                    Bootcamp bootcamp = bootcampMapper.bootcampDTOTobBootcamp(bootcampDTO);
                    log.info("[{}] Mapped Bootcamp: name={}, capacitiesIds={}", messageId, bootcamp.getName(), bootcamp.getCapacitiesIds());
                    return bootcampServicePort.registerBootcamp(bootcamp)
                            .doOnSuccess(savedBootcamp -> log.info("[{}] Bootcamp created successfully", messageId));
                })
                .flatMap(savedBootcamp -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue("Bootcamp created successfully"))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("[{}] Unexpected error: {}", messageId, ex.getMessage(), ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code("500")
                                    .message("Internal server error: " + ex.getMessage())
                                    .build()));
                });
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, String identifier, TechnicalMessage error,
                                                    List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            APIResponse apiErrorResponse = APIResponse
                    .builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .identifier(identifier)
                    .date(Instant.now().toString())
                    .errors(errors)
                    .build();
            return ServerResponse.status(httpStatus)
                    .bodyValue(apiErrorResponse);
        });
    }

    private String getMessageId(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader("X-Message-ID");
    }
}
