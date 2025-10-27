package mocviet.controller.delivery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.UserNotificationRepository;
import mocviet.repository.UserRepository;

@RestController
@RequestMapping("/delivery/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DELIVERY')")
public class DeliveryNotificationApiController {

    private final UserNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * API lấy danh sách thông báo chưa đọc.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<UserNotification>> getUnreadNotifications(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<UserNotification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * API đánh dấu một thông báo đã đọc.
     */
    @PostMapping("/notifications/{id}/mark-read")
    @Transactional
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Integer id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Map<String, Object> response = new HashMap<>();
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<UserNotification> notiOpt = notificationRepository.findByIdAndUserId(id, currentUser.getId());
        if (notiOpt.isPresent()) {
            UserNotification notification = notiOpt.get();
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Notification not found or access denied");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * API đánh dấu tất cả thông báo đã đọc.
     */
    @PostMapping("/notifications/mark-all-read")
    @Transactional
    public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Map<String, Object> response = new HashMap<>();
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        int updatedCount = notificationRepository.markAllAsReadByUserId(currentUser.getId());
        response.put("success", true);
        response.put("updatedCount", updatedCount);
        return ResponseEntity.ok(response);
    }

    /**
     * API tạo thông báo test (chỉ để debug)
     */
    @PostMapping("/notifications/test")
    @Transactional
    public ResponseEntity<Map<String, Object>> createTestNotification(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Map<String, Object> response = new HashMap<>();
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        UserNotification testNotification = new UserNotification();
        testNotification.setUser(currentUser);
        testNotification.setTitle("Thông báo test");
        testNotification.setMessage("Đây là thông báo test để kiểm tra hệ thống thông báo hoạt động.");
        testNotification.setIsRead(false);

        notificationRepository.save(testNotification);

        response.put("success", true);
        response.put("message", "Test notification created");
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy User hiện tại từ Authentication
     */
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Lấy User từ database theo username
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            Optional<User> userOpt = userRepository.findByUsername(username);
            return userOpt.orElse(null);
        }
        
        return null;
    }
}