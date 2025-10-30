package mocviet.controller.admin.users;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UsersController {

    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý users");
        model.addAttribute("activeMenu", "users");

        // Kiểm tra có phải là request của AJAX
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/users/admin_users ::content";
        }

        return "admin/users/admin_users";
    }
}
