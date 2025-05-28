package accel_tech.net.objectstorage_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OpenShiftIntegrationException extends RuntimeException {
    public OpenShiftIntegrationException(String message) {
        super(message);
    }

    public OpenShiftIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}