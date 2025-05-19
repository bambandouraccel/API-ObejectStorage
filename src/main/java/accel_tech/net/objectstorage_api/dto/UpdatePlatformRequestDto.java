package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class UpdatePlatformRequestDto {
    private Boolean isActive;
    private String apiToken;
    private String apiUrl;

}