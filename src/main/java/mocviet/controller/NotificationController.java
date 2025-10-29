package mocviet.controller;

import lombok.RequiredArgsConstructor;
import mocviet.repository.UserNotificationRepository;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.dto.customer.UserNotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    
    private final UserNotificationRepository notificationRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    /**
     * Trang hiển thị danh sách thông báo
     */
    @GetMapping("/notifications")
    public String showNotificationsPage(Model model) {
        var currentUser = userDetailsService.getCurrentUser();
        if (currentUser != null) {
            var notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(currentUser.getId());
            // Map entity -> DTO nhẹ để view
            List<UserNotificationDTO> dtoList = notifications.stream().map(n -> {
                UserNotificationDTO dto = new UserNotificationDTO();
                dto.setId(n.getId());
                dto.setTitle(n.getTitle());
                dto.setContent(n.getMessage());
                dto.setIsRead(n.getIsRead());
                dto.setCreatedAt(n.getCreatedAt());
                return dto;
            }).toList();
            model.addAttribute("notifications", dtoList);
        }
        return "notifications";
    }
    
    /**
     * API: Lấy tất cả notifications chưa đọc của user hiện tại
     * Sắp xếp từ cũ nhất đến mới nhất
     */
    @GetMapping("/api/notifications/unread")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadNotifications() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var currentUser = userDetailsService.getCurrentUser();
            if (currentUser != null) {
                var notifications = notificationRepository
                    .findByUserIdAndIsReadFalseOrderByCreatedAtAsc(currentUser.getId());

                response.put("success", true);
                response.put("notifications", notifications.stream().map(n -> {
                    Map<String, Object> notifMap = new HashMap<>();
                    notifMap.put("id", n.getId());
                    notifMap.put("title", n.getTitle());
                    notifMap.put("message", n.getMessage());
                    notifMap.put("isRead", n.getIsRead());
                    notifMap.put("createdAt", n.getCreatedAt().toString());
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
    @PostMapping("/api/notifications/{notificationId}/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Integer notificationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.ok(response);
            }
            
            var notification = notificationRepository.findById(notificationId).orElse(null);
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
    @PostMapping("/api/notifications/read-all")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var currentUser = userDetailsService.getCurrentUser();
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.ok(response);
            }
            
            notificationRepository.markAllAsReadByUserId(currentUser.getId());
            
            response.put("success", true);
            response.put("message", "All notifications marked as read");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}

