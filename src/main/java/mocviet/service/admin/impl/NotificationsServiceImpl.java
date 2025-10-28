package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.dto.admin.NotificationDTO;
import mocviet.dto.admin.NotificationsResponseDTO;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.UserNotificationRepository;
import mocviet.service.admin.NotificationsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationsServiceImpl implements NotificationsService {
    
    private final UserNotificationRepository notificationRepository;
    
    @Override
    public NotificationsResponseDTO getNotifications(User user) {
        log.info("Fetching notifications for user: {}", user.getUsername());
        
        List<UserNotification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        long totalCount = notificationRepository.countByUser(user);
        long unreadCount = notificationRepository.countByUserAndIsRead(user, false);
        long readCount = totalCount - unreadCount;
        
        return NotificationsResponseDTO.builder()
                .notifications(notificationDTOs)
                .totalCount(totalCount)
                .unreadCount(unreadCount)
                .readCount(readCount)
                .build();
    }
    
    @Override
    @Transactional
    public void markAsRead(Integer notificationId, User user) {
        log.info("Marking notification {} as read for user: {}", notificationId, user.getUsername());
        
        UserNotification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void markAllAsRead(User user) {
        log.info("Marking all notifications as read for user: {}", user.getUsername());
        
        List<UserNotification> unreadNotifications = notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false);
        
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    @Override
    @Transactional
    public void deleteNotification(Integer notificationId, User user) {
        log.info("Deleting notification {} for user: {}", notificationId, user.getUsername());
        
        UserNotification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notificationRepository.delete(notification);
    }
    
    @Override
    @Transactional
    public void deleteAllRead(User user) {
        log.info("Deleting all read notifications for user: {}", user.getUsername());
        
        List<UserNotification> readNotifications = notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, true);
        notificationRepository.deleteAll(readNotifications);
    }
    
    private NotificationDTO mapToDTO(UserNotification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

