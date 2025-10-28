package mocviet.service.admin;

import mocviet.dto.admin.NotificationsResponseDTO;
import mocviet.entity.User;

public interface NotificationsService {
    
    NotificationsResponseDTO getNotifications(User user);
    
    void markAsRead(Integer notificationId, User user);
    
    void markAllAsRead(User user);
    
    void deleteNotification(Integer notificationId, User user);
    
    void deleteAllRead(User user);
}

