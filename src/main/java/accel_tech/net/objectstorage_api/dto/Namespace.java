package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class Namespace {
    private String apiVersion = "v1";
    private String kind = "Namespace";
    private Metadata metadata;
}
