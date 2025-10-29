package mocviet.controller.delivery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mocviet.dto.delivery.NotificationDTO;
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
     * API lấy danh sách thông báo chưa đọc với phân trang.
     */
    @GetMapping("/notifications/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<UserNotification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(currentUser.getId());
        
        // Chuyển đổi sang DTO và phân trang thủ công
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
        
        int start = page * size;
        int end = Math.min(start + size, notificationDTOs.size());
        List<NotificationDTO> pagedNotifications = notificationDTOs.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", pagedNotifications);
        response.put("totalUnread", notificationDTOs.size());
        response.put("currentPage", page);
        response.put("totalPages", (int) Math.ceil((double) notificationDTOs.size() / size));
        
        return ResponseEntity.ok(response);
    }

    /**
     * API lấy tất cả thông báo (đã đọc và chưa đọc) với phân trang.
     */
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserNotification> notificationPage = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);
        
        List<NotificationDTO> notificationDTOs = notificationPage.getContent().stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationDTOs);
        response.put("totalElements", notificationPage.getTotalElements());
        response.put("totalPages", notificationPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * API đếm số thông báo chưa đọc.
     */
    @GetMapping("/notifications/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        
        return ResponseEntity.ok(response);
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
                response.put("success", true);
                response.put("message", "Thông báo đã được đánh dấu là đã đọc");
            } else {
                response.put("success", true);
                response.put("message", "Thông báo đã được đọc trước đó");
            }
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Không tìm thấy thông báo hoặc không có quyền truy cập");
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
        response.put("message", "Đã đánh dấu " + updatedCount + " thông báo là đã đọc");
        
        return ResponseEntity.ok(response);
    }

    /**
     * API xóa một thông báo.
     */
    @DeleteMapping("/notifications/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Integer id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Map<String, Object> response = new HashMap<>();
        
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<UserNotification> notiOpt = notificationRepository.findByIdAndUserId(id, currentUser.getId());
        if (notiOpt.isPresent()) {
            notificationRepository.deleteByIdAndUserId(id, currentUser.getId());
            response.put("success", true);
            response.put("message", "Thông báo đã được xóa thành công");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Không tìm thấy thông báo hoặc không có quyền truy cập");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * API tạo thông báo test (chỉ để debug).
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
        testNotification.setTitle("Thông báo test từ hệ thống");
        testNotification.setMessage("Đây là thông báo test để kiểm tra hệ thống thông báo hoạt động. Thời gian: " + 
                java.time.LocalDateTime.now().toString());
        testNotification.setIsRead(false);

        notificationRepository.save(testNotification);

        response.put("success", true);
        response.put("message", "Thông báo test đã được tạo thành công");
        response.put("notificationId", testNotification.getId());
        
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
        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            // Lấy User từ database theo username
            String username = userDetails.getUsername();
            Optional<User> userOpt = userRepository.findByUsername(username);
            return userOpt.orElse(null);
        }
        
        return null;
    }
}