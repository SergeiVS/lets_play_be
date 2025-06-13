package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/v1/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public interface SseNotificationControllerApi {

    @Operation(summary = "Opening of SSE connection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sse connection were created",
                    content = {@Content(mediaType = "text/event-stream",
                            schema = @Schema(implementation = SseEmitter.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter openSseStream(Authentication authentication) throws IOException;




}
