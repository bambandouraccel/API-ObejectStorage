package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Metadata {
        private String name;
        private String namespace;
        private String uid;
        private Map<String, String> labels;
}
