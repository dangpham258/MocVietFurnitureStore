package mocviet.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsResponseDTO {
    
    private List<NotificationDTO> notifications;
    private long totalCount;
    private long unreadCount;
    private long readCount;
}

