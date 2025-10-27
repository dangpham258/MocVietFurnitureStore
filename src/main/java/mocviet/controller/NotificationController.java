package mocviet.controller;

import lombok.RequiredArgsConstructor;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.UserNotificationRepository;
import mocviet.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final UserNotificationRepository notificationRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    /**
     * API: Lấy tất cả notifications chưa đọc của user hiện tại
     * Sắp xếp từ cũ nhất đến mới nhất
     */
    @GetMapping("/unread")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadNotifications() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser != null) {
                // Lấy notifications chưa đọc, sắp xếp từ cũ đến mới
                List<UserNotification> notifications = notificationRepository
                    .findByUserAndIsReadFalseOrderByCreatedAtAsc(currentUser);
                
                response.put("success", true);
                response.put("notifications", notifications.stream().map(notif -> {
                    Map<String, Object> notifMap = new HashMap<>();
                    notifMap.put("id", notif.getId());
                    notifMap.put("title", notif.getTitle());
                    notifMap.put("message", notif.getMessage());
                    notifMap.put("isRead", notif.getIsRead());
                    notifMap.put("createdAt", notif.getCreatedAt().toString());
                    return notifMap;
                }).toList());
            } else {
                response.put("success", false);
                response.put("notifications", List.of());
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("notifications", List.of());
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Đánh dấu notification là đã đọc
     */
    @PostMapping("/{notificationId}/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Integer notificationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.ok(response);
            }
            
            UserNotification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification == null) {
                response.put("success", false);
                response.put("message", "Notification not found");
                return ResponseEntity.ok(response);
            }
            
            // Kiểm tra quyền sở hữu
            if (!notification.getUser().getId().equals(currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Unauthorized");
                return ResponseEntity.ok(response);
            }
            
            notification.setIsRead(true);
            notificationRepository.save(notification);
            
            response.put("success", true);
            response.put("message", "Notification marked as read");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Đánh dấu tất cả notifications là đã đọc
     */
    @PostMapping("/read-all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.ok(response);
            }
            
            notificationRepository.markAllAsRead(currentUser);
            
            response.put("success", true);
            response.put("message", "All notifications marked as read");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}

