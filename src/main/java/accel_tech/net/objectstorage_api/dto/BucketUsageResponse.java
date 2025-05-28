package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class BucketUsageResponse {
    private long objectsCount;
    private long bytesTotal;
}