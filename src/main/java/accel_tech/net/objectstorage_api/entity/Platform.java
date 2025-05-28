package accel_tech.net.objectstorage_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

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
    @Field(name = "name")
    private String name;

    @Field(name = "kind")
    private String kind;

    @Field(name = "api_url")
    private String apiUrl;

    @Field(name = "api_token")
    private String apiToken;

    @Field(name = "region")
    private String region;

    @Field(name = "is_active")
    private Boolean isActive = true;

    @Field(name = "storage_class_name")
    private String storageClassName;

    @Field(name = "global_endpoint")
    private String globalEndpoint;

    @CreatedDate
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field(name = "updated_at")
    private Date updatedAt;

}
