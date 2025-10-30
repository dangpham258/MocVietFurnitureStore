package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Message;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    private Integer id;
    private Integer conversationId;
    private Message.SenderType senderType;
    private Integer senderId;
    private String senderName; // Tên người gửi (guest name hoặc user full name)
    private String content;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    
    // Constructor để convert từ Message entity
    public ChatMessageDTO(Message message) {
        this.id = message.getId();
        this.conversationId = message.getConversation().getId();
        this.senderType = message.getSenderType();
        this.senderId = message.getSender() != null ? message.getSender().getId() : null;
        
        // Xác định tên người gửi
        if (message.getSenderType() == Message.SenderType.GUEST) {
            this.senderName = message.getConversation().getGuestName();
        } else if (message.getSender() != null) {
            this.senderName = message.getSender().getFullName() != null 
                ? message.getSender().getFullName() 
                : message.getSender().getUsername();
        }
        
        this.content = message.getContent();
        this.attachmentUrl = message.getAttachmentUrl();
        this.createdAt = message.getCreatedAt();
    }
}
