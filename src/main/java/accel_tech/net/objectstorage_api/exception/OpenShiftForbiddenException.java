package accel_tech.net.objectstorage_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class OpenShiftForbiddenException extends RuntimeException {
    public OpenShiftForbiddenException(String message) {
        super(message);
    }
}