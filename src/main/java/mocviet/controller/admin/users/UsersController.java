package mocviet.controller.admin.users;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UsersController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model) {
        model.addAttribute("pageTitle", "Quản lý users");
        model.addAttribute("activeMenu", "users");
        return "admin/users/admin_users";
    }
}
