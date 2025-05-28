package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class BucketCreationDto {
    private String bucketName;
    private String region;
    private String projectId;
}