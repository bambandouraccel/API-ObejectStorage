package accel_tech.net.objectstorage_api.exception;

import accel_tech.net.objectstorage_api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", new Date());
        details.put("path", request.getDescription(false));

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "RESOURCE_NOT_FOUND",
                Collections.emptyList(),
                details);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalExceptionHandler(Exception ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", new Date());
        details.put("path", request.getDescription(false));

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "INTERNAL_SERVER_ERROR",
                Collections.emptyList(),
                details);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", new Date());
        details.put("path", request.getDescription(false));

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "BAD_REQUEST",
                Collections.emptyList(),
                details);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
