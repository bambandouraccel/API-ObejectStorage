package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class UpdatePlatformRequestDto {
    private Boolean isActive;
    private String name;
    private String kind;
    private String apiUrl;

}