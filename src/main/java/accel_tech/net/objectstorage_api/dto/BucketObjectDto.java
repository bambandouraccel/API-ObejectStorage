package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class BucketObjectDto {
    private String name;
    private long size;
    private Instant dateModified;
}