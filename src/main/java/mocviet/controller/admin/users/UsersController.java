package mocviet.controller.admin.users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UsersController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý users");
        model.addAttribute("activeMenu", "users");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/users/admin_users ::content";
        }
        
        return "admin/users/admin_users";
    }
}
