package mocviet.controller.manager;

import lombok.RequiredArgsConstructor;
import mocviet.entity.User;
import mocviet.repository.UserRepository;
import mocviet.service.manager.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/notifications")
@RequiredArgsConstructor
public class ManagerNotificationController {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    
    /**
     * Trang danh sách tất cả thông báo
     */
    @GetMapping("")
    @PreAuthorize("hasRole('MANAGER')")
    public String allNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication,
            Model model) {
        
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        var notificationsPage = notificationService.getNotifications(currentUser, page, size);
        var unreadCount = notificationService.getUnreadCount(currentUser);
        
        model.addAttribute("notifications", notificationsPage);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationsPage.getTotalPages());
        model.addAttribute("activeMenu", "notifications");
        model.addAttribute("pageTitle", "Thông báo");
        model.addAttribute("notificationService", notificationService); // Để generate link trong template
        
        return "manager/notifications/all";
    }
    
    /**
     * API: Đánh dấu một thông báo là đã đọc
     */
    @PostMapping("/{id}/mark-read")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseBody
    public String markAsRead(@PathVariable Integer id, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        notificationService.markAsRead(id, currentUser);
        return "success";
    }
    
    /**
     * API: Đánh dấu tất cả thông báo là đã đọc
     */
    @PostMapping("/mark-all-read")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseBody
    public String markAllAsRead(Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        notificationService.markAllAsRead(currentUser);
        return "success";
    }
    
    /**
     * API: Lấy số lượng thông báo chưa đọc (cho badge counter)
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseBody
    public Long getUnreadCount(Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return notificationService.getUnreadCount(currentUser);
    }
}

