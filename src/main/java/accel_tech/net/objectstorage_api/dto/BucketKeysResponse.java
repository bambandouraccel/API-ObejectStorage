package accel_tech.net.objectstorage_api.dto;

import lombok.Data;

@Data
public class BucketKeysResponse {
    private BucketKeysDto data;


    public String getAccessKey(){
        return this.data.AWS_ACCESS_KEY_ID;
    }

    public String getSecretKey(){
        return this.data.AWS_SECRET_ACCESS_KEY;
    }

}

