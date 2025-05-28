package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class BucketCreationResponse {
    private String platformId;
    private String bucketId;
    private String endpoint;
}