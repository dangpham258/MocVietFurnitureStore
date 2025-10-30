package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Conversation;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    
    private Integer id;
    private Integer userId;
    private String guestName;
    private String guestEmail;
    private Conversation.ConversationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    
    // Thông tin người dùng (nếu là customer)
    private String userName;
    private String userEmail;
    
    // Thống kê
    private Long unreadCount; // Số tin nhắn chưa đọc (cho manager)
    private ChatMessageDTO lastMessage; // Tin nhắn cuối cùng
    
    public ConversationDTO(Conversation conversation) {
        this.id = conversation.getId();
        this.userId = conversation.getUser() != null ? conversation.getUser().getId() : null;
        this.guestName = conversation.getGuestName();
        this.guestEmail = conversation.getGuestEmail();
        this.status = conversation.getStatus();
        this.createdAt = conversation.getCreatedAt();
        this.closedAt = conversation.getClosedAt();
        
        if (conversation.getUser() != null) {
            this.userName = conversation.getUser().getFullName() != null 
                ? conversation.getUser().getFullName() 
                : conversation.getUser().getUsername();
            this.userEmail = conversation.getUser().getEmail();
        }
    }
}
