package mocviet.controller.admin.notifications;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class NotificationsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String notifications(Model model) {
        model.addAttribute("pageTitle", "Quản lý thông báo");
        model.addAttribute("activeMenu", "notifications");
        return "admin/notifications/admin_notifications";
    }
}
