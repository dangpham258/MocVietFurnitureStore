package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String message;
    private boolean success;

    public MessageResponse(String message) {
        this.message = message;
        this.success = true;
    }

    public static MessageResponse success(String message) {
        MessageResponse response = new MessageResponse();
        response.setMessage(message);
        response.setSuccess(true);
        return response;
    }

    public static MessageResponse error(String message) {
        MessageResponse response = new MessageResponse();
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }
}

