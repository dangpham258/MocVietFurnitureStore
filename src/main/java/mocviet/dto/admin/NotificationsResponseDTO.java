package mocviet.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

