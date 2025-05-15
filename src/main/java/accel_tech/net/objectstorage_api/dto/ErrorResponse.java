package accel_tech.net.objectstorage_api.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ErrorResponse {
    private boolean success = false;
    private ErrorDetails error;

    public ErrorResponse(String message, String type, List<String> fields, Map<String, Object> details) {
        this.error = new ErrorDetails(message, type, fields, details);
    }

    @Data
    public static class ErrorDetails {
        private String message;
        private String type;
        private List<String> fields;
        private Map<String, Object> details;

        public ErrorDetails(String message, String type, List<String> fields, Map<String, Object> details) {
            this.message = message;
            this.type = type;
            this.fields = fields;
            this.details = details;
        }
    }
}


