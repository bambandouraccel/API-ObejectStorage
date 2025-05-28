package accel_tech.net.objectstorage_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ObjectOperationRequest {
    private String bucketName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String platformId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String projectId;

    private String objectName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String key;

    private String bucketId;
}