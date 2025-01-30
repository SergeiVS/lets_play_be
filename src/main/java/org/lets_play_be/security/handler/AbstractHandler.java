package org.lets_play_be.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class AbstractHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    protected void fillResponse(HttpServletResponse response, int status, String message) {
        response.setContentType("application/json");
        response.setStatus(status);
        try {
            response.getWriter().write(mapper.writeValueAsString(message));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
