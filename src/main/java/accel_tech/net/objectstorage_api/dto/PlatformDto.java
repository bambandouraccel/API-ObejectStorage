package accel_tech.net.objectstorage_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDto {
    @JsonProperty("_id")
    private String _id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("kind")
    private String kind;
    @JsonProperty("apiUrl")
    private String apiUrl;
    @JsonProperty("isActive")
    private Boolean isActive;

}
