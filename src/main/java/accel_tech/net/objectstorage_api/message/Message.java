package accel_tech.net.objectstorage_api.message;

import lombok.*;

@Data
public class Message {

    private String message;

    public Message() {}

    public Message(String message) {
        this.message = message;
    }

}