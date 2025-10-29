package mocviet.service.manager;

import mocviet.entity.User;
import mocviet.entity.UserNotification;
import org.springframework.data.domain.Page;

public interface INotificationService {
    Page<UserNotification> getNotifications(User user, int page, int size);
    Long getUnreadCount(User user);
    void markAsRead(Integer notificationId, User user);
    void markAllAsRead(User user);
    java.util.List<UserNotification> getUnreadNotifications(User user);
    String generateLinkFromNotification(UserNotification notification);
}


