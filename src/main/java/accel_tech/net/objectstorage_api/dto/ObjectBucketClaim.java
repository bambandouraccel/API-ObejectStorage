package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class ObjectBucketClaim {
    private String apiVersion = "objectbucket.io/v1alpha1";
    private String kind = "ObjectBucketClaim";
    private Metadata metadata;
    private Spec spec;
}
