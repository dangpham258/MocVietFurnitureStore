package mocviet.controller.admin.notifications;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.NotificationsResponseDTO;
import mocviet.entity.User;
import mocviet.service.admin.NotificationsService;

@RestController
@RequestMapping("/admin/notifications/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NotificationsApiController {

    private final NotificationsService notificationsService;

    @GetMapping
    public ResponseEntity<NotificationsResponseDTO> getNotifications() {
        User user = getCurrentUser();
        NotificationsResponseDTO response = notificationsService.getNotifications(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Integer id) {
        User user = getCurrentUser();
        notificationsService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead() {
        User user = getCurrentUser();
        notificationsService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Integer id) {
        User user = getCurrentUser();
        notificationsService.deleteNotification(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-all-read")
    public ResponseEntity<?> deleteAllRead() {
        User user = getCurrentUser();
        notificationsService.deleteAllRead(user);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }
}

