package accel_tech.net.objectstorage_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDto {
    @JsonProperty("_id")
    private String _id;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    @JsonProperty("name")
    private String name;

    @Pattern(regexp = "^(kubernetes)$", message = "Only 'kubernetes' is supported as platform kind")
    @JsonProperty("kind")
    private String kind;

    @JsonProperty("apiUrl")
    @NotBlank(message = "API URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "API URL must start with http:// or https://")
    @URL(message = "API URL must be a valid URL")
    private String apiUrl;

    @NotBlank(message = "Storage Class Name is required")
    @JsonProperty("storageClassName")
    private String storageClassName;

    @Pattern(regexp = "^(http|https)://.*$", message = "Global Endpoint must start with http:// or https://")
    @URL(message = "Global Endpoint must be a valid URL")
    @NotBlank(message = "Global Endpoint is required")
    @JsonProperty("globalEndpoint")
    private String globalEndpoint;

    @JsonProperty("region")
    private String region;

    @JsonProperty("isActive")
    private Boolean isActive;

    @NotBlank(message = "API Token is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String apiToken;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date createdAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updatedAt;

}
