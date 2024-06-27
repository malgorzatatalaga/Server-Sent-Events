package com.ssestockprices.api.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        Throwable error = getError(request);

        if (error instanceof InvalidDateException) {
            errorAttributes.put("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.put("error", "Bad Request");
            errorAttributes.put("message", error.getMessage());
        } else if (error instanceof ResponseStatusException) {
            HttpStatus status = (HttpStatus) ((ResponseStatusException) error).getStatusCode();
            errorAttributes.put("status", status.value());
            errorAttributes.put("error", status.getReasonPhrase());
            errorAttributes.put("message", ((ResponseStatusException) error).getReason());
        } else {
            errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put("error", "Internal Server Error");
            errorAttributes.put("message", "An unexpected error occurred");
        }

        return errorAttributes;
    }
}
