package accel_tech.net.objectstorage_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "Platforms")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Platform implements Serializable {

    @Id
    private String _id;

    @Indexed(unique = true)
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    @Field(name = "Name")
    private String name;

    @Pattern(regexp = "^(kubernetes)$", message = "Invalid platform kind")
    @Field(name = "kind")
    private String kind;

    @URL(message = "API URL must be a valid URL")
    @Field(name = "api_url")
    private String apiUrl;

    @NotBlank(message = "API token is required")
    @Field(name = "api_token")
    private String apiToken;

    @Field(name = "is_active")
    private Boolean isActive = true; // Default Value

    @CreatedDate
    @Field(name = "Created_At")
    private Date createdAt;

    @LastModifiedDate
    @Field(name = "Updated_At")
    private Date updatedAt;

}
