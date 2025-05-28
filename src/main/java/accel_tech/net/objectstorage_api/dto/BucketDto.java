package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class BucketDto {
    private String bucketName;
    private  String namespace;
    private  String storageClassName;

}
