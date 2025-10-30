package mocviet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageDTO {
    
    private Integer conversationId; // NULL nếu tạo conversation mới
    
    // Thông tin guest (nếu là guest)
    private String guestName;
    private String guestEmail;
    // Thông tin người gửi (nếu là customer)
    private Integer senderId;
    
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    @Size(max = 2000, message = "Nội dung không được vượt quá 2000 ký tự")
    private String content;
    
    private String attachmentUrl; // URL ảnh sau khi upload
}
